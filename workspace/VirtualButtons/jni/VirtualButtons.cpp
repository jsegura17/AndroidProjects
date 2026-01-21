/*==============================================================================
            Copyright (c) 2011 QUALCOMM Austria Research Center GmbH.
            All Rights Reserved.
            Qualcomm Confidential and Proprietary
            
@file 
    VirtualButtons.cpp

@brief
    Sample for VirtualButtons

==============================================================================*/


#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include <QCAR/QCAR.h>
#include <QCAR/UpdateCallback.h>
#include <QCAR/CameraDevice.h>
#include <QCAR/Renderer.h>
#include <QCAR/Area.h>
#include <QCAR/Rectangle.h>
#include <QCAR/VideoBackgroundConfig.h>
#include <QCAR/Trackable.h>
#include <QCAR/Tool.h>
#include <QCAR/Tracker.h>
#include <QCAR/TrackerManager.h>
#include <QCAR/ImageTracker.h>
#include <QCAR/CameraCalibration.h>
#include <QCAR/ImageTarget.h>
#include <QCAR/VirtualButton.h>
#include <QCAR/DataSet.h>

#include "SampleUtils.h"
#include "Texture.h"
#include "CubeShaders.h"
#include "LineShaders.h"
#include "Teapot.h"

#ifdef __cplusplus
extern "C"
{
#endif

// Textures:
int textureCount                = 0;
Texture** textures              = 0;

// OpenGL ES 2.0 specific (3D model):
unsigned int shaderProgramID    = 0;
GLint vertexHandle              = 0;
GLint normalHandle              = 0;
GLint textureCoordHandle        = 0;
GLint mvpMatrixHandle           = 0;

// OpenGL ES 2.0 specific (Virtual Buttons):
unsigned int vbShaderProgramID  = 0;
GLint vbVertexHandle            = 0;

// Screen dimensions:
unsigned int screenWidth        = 0;
unsigned int screenHeight       = 0;

// Indicates whether screen is in portrait (true) or landscape (false) mode
bool isActivityInPortraitMode   = false;

// The projection matrix used for rendering virtual objects:
QCAR::Matrix44F projectionMatrix;

// Constants:
static const float kTeapotScale = 3.f;

QCAR::DataSet* dataSet          = 0;

// Enumeration for masking button indices into single integer:
enum BUTTONS
{
    BUTTON_1                    = 1,
    BUTTON_2                    = 2,
    BUTTON_3                    = 4,
    BUTTON_4                    = 8
};

int buttonMask                  = 0;

// Virtual Button runtime creation:
bool updateBtns                   = false;
const char* virtualButtonColors[] = {"red", "blue", "yellow", "green"};
const int NUM_BUTTONS             = 4;


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_setActivityPortraitMode(JNIEnv *, jobject, jboolean isPortrait)
{
    isActivityInPortraitMode = isPortrait;
}

JNIEXPORT int JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_initTracker(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_ImageTargets_ImageTargets_initTracker");
    
    // Initialize the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::Tracker* tracker = trackerManager.initTracker(QCAR::Tracker::IMAGE_TRACKER);
    if (tracker == NULL)
    {
        LOG("Failed to initialize ImageTracker.");
        return 0;
    }

    LOG("Successfully initialized ImageTracker.");
    return 1;
}


JNIEXPORT void JNICALL
    Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_deinitTracker(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_ImageTargets_ImageTargets_deinitTracker");

    // Deinit the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    trackerManager.deinitTracker(QCAR::Tracker::IMAGE_TRACKER);
}




JNIEXPORT int JNICALL
    Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_loadTrackerData(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_loadTrackerData");

    // Get the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::ImageTracker* imageTracker = static_cast<QCAR::ImageTracker*>(
        trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER));
    if (imageTracker == NULL)
    {
        LOG("Failed to load tracking data set because the ImageTracker has not"
            " been initialized.");
        return 0;
    }

    // Create the data set:
    dataSet = imageTracker->createDataSet();
    if (dataSet == 0)
    {
        LOG("Failed to create a new tracking data.");
        return 0;
    }

    // Load the data set:
    if (!dataSet->load("Wood.xml", QCAR::DataSet::STORAGE_APPRESOURCE))
    {
        LOG("Failed to load data set.");
        return 0;
    }

    // Activate the data set:
    if (!imageTracker->activateDataSet(dataSet))
    {
        LOG("Failed to activate data set.");
        return 0;
    }

    LOG("Successfully loaded and activated data set.");
    return 1;
}


JNIEXPORT int JNICALL
    Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_destroyTrackerData(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_destroyTrackerData");

    // Get the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::ImageTracker* imageTracker = static_cast<QCAR::ImageTracker*>(
        trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER));
    if (imageTracker == NULL)
    {
        LOG("Failed to destroy the tracking data set because the ImageTracker has not"
            " been initialized.");
        return 0;
    }

    if (dataSet != 0)
    {
        if (!imageTracker->deactivateDataSet(dataSet))
        {
            LOG("Failed to destroy the tracking data set because the data set "
                "could not be deactivated.");
            return 0;
        }

        if (!imageTracker->destroyDataSet(dataSet))
        {
            LOG("Failed to destroy the tracking data set.");
            return 0;
        }

        LOG("Successfully destroyed the data set.");
        dataSet = 0;
        return 1;
    }

    LOG("No tracker data set to destroy.");
    return 0;
}


// Add a button to the list of buttons which are toggled in the next update call
JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_addButtonToToggle(JNIEnv */*env*/,
                                                                              jobject /*obj*/,
                                                                              jint virtualButtonIdx)
{
    LOG("Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_addButtonToToggle");

    assert(virtualButtonIdx >= 0 && virtualButtonIdx < NUM_BUTTONS);

    switch (virtualButtonIdx)
    {
        case 0:
            buttonMask |= BUTTON_1;
            break;
        
        case 1:
            buttonMask |= BUTTON_2;
            break;
        
        case 2:
            buttonMask |= BUTTON_3;
            break;
        
        case 3:
            buttonMask |= BUTTON_4;
            break;    
    }
    updateBtns = true;
}


// Create/destroy a Virtual Button at runtime
//
// Note: This will NOT work if the tracker is active!
bool
toggleVirtualButton(QCAR::ImageTarget* imageTarget, const char* name,
                    float left, float top, float right, float bottom)
{
    LOG("toggleVirtualButton");
                
    bool buttonToggleSuccess = false;
    
    QCAR::VirtualButton* virtualButton = imageTarget->getVirtualButton(name);
    if (virtualButton != NULL)
    {
        LOG("Destroying Virtual Button");
        buttonToggleSuccess = imageTarget->destroyVirtualButton(virtualButton);
    }
    else
    {
        LOG("Creating Virtual Button");
        QCAR::Rectangle vbRectangle(left, top, right, bottom);
        QCAR::VirtualButton* virtualButton = imageTarget->createVirtualButton(name, vbRectangle);

        // This is just a showcase. The values used here a set by default on Virtual Button creation
        virtualButton->setEnabled(true);
        virtualButton->setSensitivity(QCAR::VirtualButton::MEDIUM);

        if (virtualButton != NULL)
            buttonToggleSuccess = true;
    }
    
    return buttonToggleSuccess;
}


// Object to receive update callbacks from QCAR SDK
class VirtualButton_UpdateCallback : public QCAR::UpdateCallback
{    
    virtual void QCAR_onUpdate(QCAR::State& /*state*/)
    {
        if (updateBtns)
        {
            // Update runs in the tracking thread therefore it is guaranteed that the tracker is
            // not doing anything at this point. => Reconfiguration is possible.

            QCAR::ImageTracker* it = reinterpret_cast<QCAR::ImageTracker*>(
                QCAR::TrackerManager::getInstance().getTracker(QCAR::Tracker::IMAGE_TRACKER));
            assert(dataSet);
            
            // Deactivate the data set prior to reconfiguration:
            it->deactivateDataSet(dataSet);

            assert(dataSet->getNumTrackables() > 0);
            QCAR::Trackable* trackable = dataSet->getTrackable(0);
            
            assert(trackable);
            assert(trackable->getType() == QCAR::Trackable::IMAGE_TARGET);
            QCAR::ImageTarget* imageTarget = static_cast<QCAR::ImageTarget*>(trackable);
            

            if (buttonMask & BUTTON_1)
            {
                LOG("Toggle Button 1");
                
                toggleVirtualButton(imageTarget, virtualButtonColors[0],
                                    -108.68f, -53.52f, -75.75f, -65.87f);
                                    
            }
            if (buttonMask & BUTTON_2)
            {
                LOG("Toggle Button 2");
                
                toggleVirtualButton(imageTarget, virtualButtonColors[1], 
                                    -45.28f, -53.52f, -12.35f, -65.87f);
            }
            if (buttonMask & BUTTON_3)
            {
                LOG("Toggle Button 3");
                
                toggleVirtualButton(imageTarget, virtualButtonColors[2], 
                                    14.82f, -53.52f, 47.75f, -65.87f);
            }
            if (buttonMask & BUTTON_4)
            {
                LOG("Toggle Button 4");
                
                toggleVirtualButton(imageTarget, virtualButtonColors[3], 
                                    76.57f, -53.52f, 109.50f, -65.87f);
            }

            // Reactivate the data set:
            it->activateDataSet(dataSet);

            buttonMask = 0;
            updateBtns = false;
        }
    }
} qcarUpdate;


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtonsRenderer_renderFrame(JNIEnv *, jobject)
{
    //LOG("Java_com_qualcomm_QCARSamples_VirtualButtons_GLRenderer_renderFrame");
 
    // Clear color and depth buffer 
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Get the state from QCAR and mark the beginning of a rendering section
    QCAR::State state = QCAR::Renderer::getInstance().begin();    

    // Explicitly render the Video Background
    QCAR::Renderer::getInstance().drawVideoBackground();
    
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);

    // Did we find any trackables this frame?
    if (state.getNumActiveTrackables())
    {
        // Get the trackable:
        const QCAR::Trackable* trackable = state.getActiveTrackable(0);
        QCAR::Matrix44F modelViewMatrix =
            QCAR::Tool::convertPose2GLMatrix(trackable->getPose()); 

        // The image target:
        assert(trackable->getType() == QCAR::Trackable::IMAGE_TARGET);
        const QCAR::ImageTarget* target =
            static_cast<const QCAR::ImageTarget*>(trackable);

        // Set transformations:
        QCAR::Matrix44F modelViewProjection;
        SampleUtils::multiplyMatrix(&projectionMatrix.data[0],
                                    &modelViewMatrix.data[0],
                                    &modelViewProjection.data[0]);
                                    
                                    
        // Set the texture used for the teapot model:
        int textureIndex = 0;

        GLfloat vbVertices[96];
        unsigned char vbCounter=0;

        // Iterate through this targets virtual buttons:
        for (int i = 0; i < target->getNumVirtualButtons(); ++i)
        {
            const QCAR::VirtualButton* button = target->getVirtualButton(i);

            // If the button is pressed, than use this texture:
            if (button->isPressed())
            {
                // Run through button name array to find texture index
                for (int j = 0; j < NUM_BUTTONS; ++j)
                {
                    if (strcmp(button->getName(), virtualButtonColors[j]) == 0)
                    {
                        textureIndex = j+1;
                        break;
                    }
                }
            }            
            
            const QCAR::Area* vbArea = &button->getArea();
            assert(vbArea->getType() == QCAR::Area::RECTANGLE);
            const QCAR::Rectangle* vbRectangle = static_cast<const QCAR::Rectangle*>(vbArea);


            // We add the vertices to a common array in order to have one single 
            // draw call. This is more efficient than having multiple glDrawArray calls
            vbVertices[vbCounter   ]=vbRectangle->getLeftTopX();
            vbVertices[vbCounter+ 1]=vbRectangle->getLeftTopY();
            vbVertices[vbCounter+ 2]=0.0f;
            vbVertices[vbCounter+ 3]=vbRectangle->getRightBottomX();
            vbVertices[vbCounter+ 4]=vbRectangle->getLeftTopY();
            vbVertices[vbCounter+ 5]=0.0f;
            vbVertices[vbCounter+ 6]=vbRectangle->getRightBottomX();
            vbVertices[vbCounter+ 7]=vbRectangle->getLeftTopY();
            vbVertices[vbCounter+ 8]=0.0f;
            vbVertices[vbCounter+ 9]=vbRectangle->getRightBottomX();
            vbVertices[vbCounter+10]=vbRectangle->getRightBottomY();
            vbVertices[vbCounter+11]=0.0f;
            vbVertices[vbCounter+12]=vbRectangle->getRightBottomX();
            vbVertices[vbCounter+13]=vbRectangle->getRightBottomY();
            vbVertices[vbCounter+14]=0.0f;
            vbVertices[vbCounter+15]=vbRectangle->getLeftTopX();
            vbVertices[vbCounter+16]=vbRectangle->getRightBottomY();
            vbVertices[vbCounter+17]=0.0f;
            vbVertices[vbCounter+18]=vbRectangle->getLeftTopX();
            vbVertices[vbCounter+19]=vbRectangle->getRightBottomY();
            vbVertices[vbCounter+20]=0.0f;
            vbVertices[vbCounter+21]=vbRectangle->getLeftTopX();
            vbVertices[vbCounter+22]=vbRectangle->getLeftTopY();
            vbVertices[vbCounter+23]=0.0f;
            vbCounter+=24;
            
        }

        // We only render if there is something on the array
        if (vbCounter>0)
        {
            // Render frame around button
            glUseProgram(vbShaderProgramID);

            glVertexAttribPointer(vbVertexHandle, 3, GL_FLOAT, GL_FALSE, 0,
                (const GLvoid*) &vbVertices[0]);

            glEnableVertexAttribArray(vbVertexHandle);

            glUniformMatrix4fv(mvpMatrixHandle, 1, GL_FALSE,
                (GLfloat*)&modelViewProjection.data[0] );

            // We multiply by 8 because that's the number of vertices per button
            // The reason is that GL_LINES considers only pairs. So some vertices
            // must be repeated.
            glDrawArrays(GL_LINES, 0, target->getNumVirtualButtons()*8); 

            SampleUtils::checkGlError("VirtualButtons drawButton");

            glDisableVertexAttribArray(vbVertexHandle);
        }

        // Assumptions:
        assert(textureIndex < textureCount);
        const Texture* const thisTexture = textures[textureIndex];                            

        
        // Scale 3D model
        QCAR::Matrix44F modelViewScaled = modelViewMatrix;
        SampleUtils::scalePoseMatrix(kTeapotScale, kTeapotScale, kTeapotScale,
                                     &modelViewScaled.data[0]);

        QCAR::Matrix44F modelViewProjectionScaled;
        SampleUtils::multiplyMatrix(&projectionMatrix.data[0],
                                    &modelViewScaled.data[0],
                                    &modelViewProjectionScaled.data[0]);
                                    
        // Render 3D model
        glUseProgram(shaderProgramID);
 
        glVertexAttribPointer(vertexHandle, 3, GL_FLOAT, GL_FALSE, 0,
                              (const GLvoid*) &teapotVertices[0]);
        glVertexAttribPointer(normalHandle, 3, GL_FLOAT, GL_FALSE, 0,
                              (const GLvoid*) &teapotNormals[0]);
        glVertexAttribPointer(textureCoordHandle, 2, GL_FLOAT, GL_FALSE, 0,
                              (const GLvoid*) &teapotTexCoords[0]);

        glEnableVertexAttribArray(vertexHandle);
        glEnableVertexAttribArray(normalHandle);
        glEnableVertexAttribArray(textureCoordHandle);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, thisTexture->mTextureID);
        glUniformMatrix4fv(mvpMatrixHandle, 1, GL_FALSE,
                           (GLfloat*)&modelViewProjectionScaled.data[0] );
        glDrawElements(GL_TRIANGLES, NUM_TEAPOT_OBJECT_INDEX, GL_UNSIGNED_SHORT,
                       (const GLvoid*) &teapotIndices[0]);

        SampleUtils::checkGlError("VirtualButtons renderFrame");

    }
    
    glDisable(GL_DEPTH_TEST);

    glDisableVertexAttribArray(vertexHandle);
    glDisableVertexAttribArray(normalHandle);
    glDisableVertexAttribArray(textureCoordHandle);

    QCAR::Renderer::getInstance().end();
}


void
configureVideoBackground()
{
    // Get the default video mode:
    QCAR::CameraDevice& cameraDevice = QCAR::CameraDevice::getInstance();
    QCAR::VideoMode videoMode = cameraDevice.
                                getVideoMode(QCAR::CameraDevice::MODE_DEFAULT);

    // Configure the video background
    QCAR::VideoBackgroundConfig config;
    config.mEnabled = true;
    config.mSynchronous = true;
    config.mPosition.data[0] = 0.0f;
    config.mPosition.data[1] = 0.0f;
    
    if (isActivityInPortraitMode)
    {
        //LOG("configureVideoBackground PORTRAIT");
        config.mSize.data[0] = videoMode.mHeight
                                * (screenHeight / (float)videoMode.mWidth);
        config.mSize.data[1] = screenHeight;

        if(config.mSize.data[0] < screenWidth)
        {
            LOG("Correcting rendering background size to handle missmatch between screen and video aspect ratios.");
            config.mSize.data[0] = screenWidth;
            config.mSize.data[1] = screenWidth * 
                              (videoMode.mWidth / (float)videoMode.mHeight);
        }
    }
    else
    {
        //LOG("configureVideoBackground LANDSCAPE");
        config.mSize.data[0] = screenWidth;
        config.mSize.data[1] = videoMode.mHeight
                            * (screenWidth / (float)videoMode.mWidth);

        if(config.mSize.data[1] < screenHeight)
        {
            LOG("Correcting rendering background size to handle missmatch between screen and video aspect ratios.");
            config.mSize.data[0] = screenHeight
                                * (videoMode.mWidth / (float)videoMode.mHeight);
            config.mSize.data[1] = screenHeight;
        }
    }

    // Set the config:
    QCAR::Renderer::getInstance().setVideoBackgroundConfig(config);
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_initApplicationNative(
                            JNIEnv* env, jobject obj, jint width, jint height)
{
    LOG("Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_initApplicationNative");
    
    // Store screen dimensions
    screenWidth = width;
    screenHeight = height;
        
    // Handle to the activity class:
    jclass activityClass = env->GetObjectClass(obj);
    
    // Register callback function that gets called every time a tracking cycle 
    // has finished and we have a new AR state avaible
    QCAR::registerCallback(&qcarUpdate);

    jmethodID getTextureCountMethodID = env->GetMethodID(activityClass,
                                                    "getTextureCount", "()I");
    if (getTextureCountMethodID == 0)
    {
        LOG("Function getTextureCount() not found.");
        return;
    }

    textureCount = env->CallIntMethod(obj, getTextureCountMethodID);    
    if (!textureCount)
    {
        LOG("getTextureCount() returned zero.");
        return;
    }

    textures = new Texture*[textureCount];

    jmethodID getTextureMethodID = env->GetMethodID(activityClass,
        "getTexture", "(I)Lcom/qualcomm/QCARSamples/VirtualButtons/Texture;");

    if (getTextureMethodID == 0)
    {
        LOG("Function getTexture() not found.");
        return;
    }

    // Register the textures
    for (int i = 0; i < textureCount; ++i)
    {

        jobject textureObject = env->CallObjectMethod(obj, getTextureMethodID, i); 
        if (textureObject == NULL)
        {
            LOG("GetTexture() returned zero pointer");
            return;
        }

        textures[i] = Texture::create(env, textureObject);
    }
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_deinitApplicationNative(
                                                        JNIEnv* env, jobject obj)
{
    LOG("Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_deinitApplicationNative");

    // Release texture resources
    if (textures != 0)
    {    
        for (int i = 0; i < textureCount; ++i)
        {
            delete textures[i];
            textures[i] = NULL;
        }
    
        delete[]textures;
        textures = NULL;
        
        textureCount = 0;
    }
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_startCamera(JNIEnv *,
                                                                         jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_startCamera");

    // Initialize the camera:
    if (!QCAR::CameraDevice::getInstance().init())
        return;

    // Configure the video background
    configureVideoBackground();

    // Select the default mode:
    if (!QCAR::CameraDevice::getInstance().selectVideoMode(
                                QCAR::CameraDevice::MODE_DEFAULT))
        return;

    // Start the camera:
    if (!QCAR::CameraDevice::getInstance().start())
        return;

    // Start the tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::Tracker* imageTracker = trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER);
    if(imageTracker != 0)
        imageTracker->start();
 
    // Cache the projection matrix:
    const QCAR::CameraCalibration& cameraCalibration =
                                QCAR::CameraDevice::getInstance().getCameraCalibration();
    projectionMatrix = QCAR::Tool::getProjectionGL(cameraCalibration, 2.0f,
                                            2000.0f);
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_stopCamera(JNIEnv *,
                                                                   jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_stopCamera");

    // Stop the tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::Tracker* imageTracker = trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER);
    if(imageTracker != 0)
        imageTracker->stop();
    
    QCAR::CameraDevice::getInstance().stop();
    QCAR::CameraDevice::getInstance().deinit();
}

JNIEXPORT jboolean JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_toggleFlash(JNIEnv*, jobject, jboolean flash)
{
    return QCAR::CameraDevice::getInstance().setFlashTorchMode((flash==JNI_TRUE)) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_autofocus(JNIEnv*, jobject)
{
    return QCAR::CameraDevice::getInstance().startAutoFocus()?JNI_TRUE:JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtons_setFocusMode(JNIEnv*, jobject, jint mode)
{
    return QCAR::CameraDevice::getInstance().setFocusMode(mode)?JNI_TRUE:JNI_FALSE;
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtonsRenderer_initRendering(
                                                    JNIEnv* env, jobject obj)
{
    LOG("Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtonsRenderer_initRendering");

    // Define clear color
    glClearColor(0.0f, 0.0f, 0.0f, QCAR::requiresAlpha() ? 0.0f : 1.0f);
    
    // Now generate the OpenGL texture objects and add settings
    for (int i = 0; i < textureCount; ++i)
    {
        glGenTextures(1, &(textures[i]->mTextureID));
        glBindTexture(GL_TEXTURE_2D, textures[i]->mTextureID);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textures[i]->mWidth,
                textures[i]->mHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE,
                (GLvoid*)  textures[i]->mData);
    }
  
    // OpenGL setup for 3D model
    shaderProgramID     = SampleUtils::createProgramFromBuffer(cubeMeshVertexShader,
                                                               cubeFragmentShader);

    vertexHandle        = glGetAttribLocation(shaderProgramID,
                                                "vertexPosition");
    normalHandle        = glGetAttribLocation(shaderProgramID,
                                                "vertexNormal");
    textureCoordHandle  = glGetAttribLocation(shaderProgramID,
                                                "vertexTexCoord");
    mvpMatrixHandle     = glGetUniformLocation(shaderProgramID,
                                                "modelViewProjectionMatrix");
                                                
    // OpenGL setup for Virtual Buttons
    vbShaderProgramID   = SampleUtils::createProgramFromBuffer(lineMeshVertexShader,
                                                               lineFragmentShader);
                                                               
    vbVertexHandle      = glGetAttribLocation(vbShaderProgramID, "vertexPosition");
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtonsRenderer_updateRendering(
                        JNIEnv* env, jobject obj, jint width, jint height)
{
    LOG("Java_com_qualcomm_QCARSamples_VirtualButtons_VirtualButtonsRenderer_updateRendering");
    
    // Update screen dimensions
    screenWidth = width;
    screenHeight = height;

    // Reconfigure the video background
    configureVideoBackground();
}


#ifdef __cplusplus
}
#endif
