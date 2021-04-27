# Robot Focus and Android Lifecycle handling

The purpose of the application is to show how to interrupt and restart an activity correctly in specific states of the Pepper QiSDK robot. These states are refered to as alive, rest and sleep modes. 

This is demo application presented in the Developer Center article [Understanding Pepper Focus and Android Lifecycle](https://developer.softbankrobotics.com/blog/understanding-pepper-focus-and-android-lifecycle).

It is based on a simple activity that plays music through Android MediaPlayer and makes Pepper dance by executing a QiSDK animation. A second activity is started when robot gets to rest mode.

## 1. Video demonstration

Video will be published soon

## 2. Running the sample app

The project comes complete. You can clone the repository, open it in Android Studio, and run this directly onto a Robot.

The sample application will play a music and start a dance.

Note that this application would be of no interest in being run on a simulated robot as rest and sleep mode cannot be accessed in this situation.

## 3. Usage: testing the different modes

You can try the different states changing the app to background and the robot mode to alive, rest and sleep. First launch the app then try the different situations as follows. All steps are detailed in log messages that help understand the robot lifecycle and android lifecycle. 

### 3.1 App in background

Sending your app to background the music and the dance stop. On resuming the music and dance start again.

### 3.2 Robot in rest mode

When you robot is alive, press twice on the chest button, the robot goes down to the rest mode (reaches the safe posture), the music and the dance stop and the tablet displays a shaded version of the background image. 

From rest mode, pressing twice on chest button robot gets back to alive mode. When robot is back to standing position, the music and dance start again.

### 3.3 Robot in sleep mode

When you robot is alive, cover simultaneously the forehead camera and the head sensor, the robot goes down to sleep mode (reaches the safe posture and dislays purple shoulder leds), the music and the dance stop and the tablet displays a black screen.

From sleep mode, on a single touch on the head sensor the robot gets back to alive mode. When robot is back to standing position, the music and dance start again.

## 4. License

This project is licensed under the BSD 3-Clause "New" or "Revised" License - see the [COPYING](COPYING.md) file for details.