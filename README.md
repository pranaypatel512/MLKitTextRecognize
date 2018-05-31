MLKitTextRecognize 
============

### App will:
Utilize the ML Kit Text Recognition API to detect text in images.

### You'll learn:
How we can use Firebase ML Kit SDK in Android with Machine learning capability.

* Get [FirebaseVisionText](https://firebase.google.com/docs/reference/android/com/google/firebase/ml/vision/text/FirebaseVisionText) object from Bitmap:
```kotlin
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
``` 
* Get texts elements result from FirebaseVisionText :
```kotlin
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
``` 
### Output:

Image One result           |Image Two result           | Image Three result            
:-------------------------:|:-------------------------:|:-------------------------:
<img align="left" height="350" src="https://github.com/pranaypatel512/MLKitTextRecognize/blob/master/screenshots/image_one_ml_kit_result.png">  |<img align="center" height="350" src="https://github.com/pranaypatel512/MLKitTextRecognize/blob/master/screenshots/image_two_ml_kit_result.png"> | <img height="350" src="https://github.com/pranaypatel512/MLKitTextRecognize/blob/master/screenshots/image_three_ml_kit_result.png">

### NOTE:
To successfully sync this project, need to set up this app into your Firebase project. 
Kindly follow below link to "Add Firebase to Your Android Project":
https://firebase.google.com/docs/android/setup 

### References: 

* [Recognize text in images with ML Kit for Firebase](https://codelabs.developers.google.com/codelabs/mlkit-android/#0)
* [ML Kit - Text Recognition](https://firebase.google.com/docs/ml-kit/recognize-text)

#### Built With

* [Android Studio](https://developer.android.com/studio/index.html) - The Official IDE for Android
* [Kotlin](https://kotlinlang.org/) - The Official Language for Android ❤️
* [Gradle](https://gradle.org/) - Build tool for Android Studio

#### Pull Requests

I welcome and encourage all pull requests to learn something new.

#### Find this project useful ? ❤️

Support it by clicking the ⭐️ button on the upper right of this page. ✌️

### Images credits
https://unsplash.com/
