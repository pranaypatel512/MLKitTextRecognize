package pranay.com.mlkittextrecognize

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {


    private var mSelectedImage: Bitmap? = null
    // Max width (portrait mode)
    private var mImageMaxWidth: Int? = null
    // Max height (portrait mode)
    private var mImageMaxHeight: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        //inti spinner items
        val items = arrayOf("Image 1", "Image 2", "Image 3")
        val adapter = ArrayAdapter(this, android.R.layout
                .simple_spinner_dropdown_item, items)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

        //setButton click
        button_text.setOnClickListener {
            runTextRecognition()
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        graphic_overlay.clear()
        when (position) {
            0 -> mSelectedImage = getBitmapFromAsset(this, "test_image_one.jpg")
            1 -> mSelectedImage = getBitmapFromAsset(this, "test_image_two.jpg")
            2 -> mSelectedImage = getBitmapFromAsset(this, "test_image_three.jpg")
        }

        mSelectedImage?.let {
            // Get the dimensions of the View
            val targetedSize = getTargetedWidthHeight()

            val targetWidth = targetedSize.first
            val maxHeight = targetedSize.second

            // Determine how much to scale down the image
            val scaleFactor = Math.max(
                    it.width.toFloat() / targetWidth.toFloat(),
                    it.height.toFloat() / maxHeight.toFloat())

            val resizedBitmap = Bitmap.createScaledBitmap(
                    it,
                    (it.width / scaleFactor).toInt(),
                    (it.height / scaleFactor).toInt(),
                    true)

            image_view.setImageBitmap(resizedBitmap)
            mSelectedImage = resizedBitmap
        }
    }

    private fun runTextRecognition() {
        val image = mSelectedImage?.let { FirebaseVisionImage.fromBitmap(it) }
        val detector = FirebaseVision.getInstance().visionTextDetector
        button_text.isEnabled = false
        image?.let {
            detector.detectInImage(it)
                    .addOnSuccessListener {
                        button_text.isEnabled = true
                        processTextRecognitionResult(it)
                    }
                    .addOnFailureListener {
                        button_text.isEnabled = true
                        showToast(it.localizedMessage)
                    }
        }


    }

    private fun processTextRecognitionResult(firebaseVisionText: FirebaseVisionText?) {
        firebaseVisionText?.let {
            val blocks = firebaseVisionText.blocks
            when (blocks.size) {
                0 -> showToast("No texts found")
                else -> {
                    graphic_overlay.clear()
                    for (block in blocks.indices) {
                        val lines = blocks[block].lines
                        for (line in lines.indices) {
                            val elements = lines[line].elements
                            for (element in elements.indices) {
                                val textGraphic = TextGraphic(graphic_overlay, elements[element])
                                graphic_overlay.add(textGraphic)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getBitmapFromAsset(context: Context, filePath: String): Bitmap? {
        val assetManager = context.assets

        val inputStream: InputStream
        var bitmap: Bitmap? = null
        try {
            inputStream = assetManager.open(filePath)
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    // Gets the targeted width / height.
    private fun getTargetedWidthHeight(): Pair<Int, Int> {
        val targetWidth: Int
        val targetHeight: Int
        val maxWidthForPortraitMode = getImageMaxWidth()!!
        val maxHeightForPortraitMode = getImageMaxHeight()!!
        targetWidth = maxWidthForPortraitMode
        targetHeight = maxHeightForPortraitMode
        return Pair(targetWidth, targetHeight)
    }
// Functions for loading images from app assets.

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
// landscape mode.
    private fun getImageMaxWidth(): Int? {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = image_view.width
        }

        return mImageMaxWidth
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
// landscape mode.
    private fun getImageMaxHeight(): Int? {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight = image_view.height
        }

        return mImageMaxHeight
    }
}

