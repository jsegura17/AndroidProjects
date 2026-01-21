/*==============================================================================
            Copyright (c) 2011 QUALCOMM Austria Research Center GmbH.
            All Rights Reserved.
            Qualcomm Confidential and Proprietary
            
@file 
    BackgroundTextureAccess.cpp

@brief
    Sample for BackgroundTextureAccess

==============================================================================*/


#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include <QCAR/QCAR.h>
#include <QCAR/CameraDevice.h>
#include <QCAR/Renderer.h>
#include <QCAR/VideoBackgroundConfig.h>
#include <QCAR/Trackable.h>
#include <QCAR/Tool.h>
#include <QCAR/Tracker.h>
#include <QCAR/TrackerManager.h>
#include <QCAR/ImageTracker.h>
#include <QCAR/CameraCalibration.h>
#include <QCAR/DataSet.h>
#include <QCAR/VideoBackgroundTextureInfo.h>

#include "SampleUtils.h"
#include "Texture.h"
#include "CubeShaders.h"
#include "Shaders.h"
#include "Teapot.h"
#include "GLUtils.h"

#ifdef __cplusplus
extern "C"
{
#endif

// Textures:
int textureCount                = 0;
Texture** textures              = 0;

// OpenGL ES 2.0 specific:
unsigned int shaderProgramID    = 0;
GLint vertexHandle              = 0;
GLint normalHandle              = 0;
GLint textureCoordHandle        = 0;
GLint mvpMatrixHandle           = 0;

// These values will hold the GL viewport
int viewportPosition_x, viewportPosition_y, viewportSize_x, viewportSize_y;

// Screen dimensions:
unsigned int screenWidth        = 0;
unsigned int screenHeight       = 0;

// Indicates whether screen is in portrait (true) or landscape (false) mode
bool isActivityInPortraitMode   = false;

// The projection matrix used for rendering virtual objects:
QCAR::Matrix44F projectionMatrix;

// Constants:
static const float kObjectScale = 3.f;

QCAR::DataSet* dataSetTarmac    = 0;

// This is the OpenGL ES index for our vertex/fragment program of the video background rendering
unsigned int vbShaderProgramID  = 0;

// These handles are required to pass the values to the videobackground shaders
GLuint vbVertexPositionHandle   = 0, vbVertexTexCoordHandle     = 0;
GLuint vbTexSampler2DHandle     = 0, vbProjectionMatrixHandle   = 0;
GLuint vbTouchLocationXHandle   = 0, vbTouchLocationYHandle     = 0;

// This flag indicates whether the shaders have been initialized
bool vbShadersInitialized       = false;

// This flag indicates whether the mesh values have been initialized
bool vbMeshInitialized          = false;

// These values indicate how many rows and columns we want for our video background texture polygon
const int vbNumVertexCols       = 10;
const int vbNumVertexRows       = 10;

// These are the variables for the vertices, coords and inidices
const int vbNumVertexValues     = vbNumVertexCols*vbNumVertexRows*3;            // Each vertex has three values: X, Y, Z
const int vbNumTexCoord         = vbNumVertexCols*vbNumVertexRows*2;            // Each texture coordinate has 2 values: U and V
const int vbNumIndices          = (vbNumVertexCols-1)*(vbNumVertexRows-1)*6;    // Each square is composed of 2 triangles which in turn 
                                                                                // have 3 vertices each, so we need 6 indices
                                                                    
// These are the data containers for the vertices, texcoords and indices in the CPU
float vbOrthoQuadVertices [vbNumVertexValues]; 
float vbOrthoQuadTexCoords [vbNumTexCoord]; 
GLbyte vbOrthoQuadIndices [vbNumIndices]; 

// This will hold the data for the projection matrix passed to the vertex shader
float vbOrthoProjMatrix [16];

// These mark the spot where the user touches the screen
float touchLocation_x, touchLocation_y;

////////////////////////////////////////////////////////////////////////////////
// This function creates the shader program with the vertex and fragment shaders
// defined in Shader.h. It also gets handles to the position of the variables
// for later usage. It also defines a standard orthographic projection matrix
void 
VBSetupShaderProgram()
{
    // Create shader program:
    if (vbShaderProgramID != 0) 
        glDeleteProgram(vbShaderProgramID);
        
    vbShaderProgramID = createShaderProgramFromBuffers(vertexShaderSrc, fragmentShaderSrc);

    if (vbShaderProgramID > 0)
    {
        // Activate shader:
        glUseProgram(vbShaderProgramID);

        // Retrieve handler for vertex position shader attribute variable:
        vbVertexPositionHandle      = glGetAttribLocation(vbShaderProgramID, "vertexPosition");

        // Retrieve handler for texture coordinate shader attribute variable:
        vbVertexTexCoordHandle      = glGetAttribLocation(vbShaderProgramID, "vertexTexCoord");

        // Retrieve handler for texture sampler shader uniform variable:
        vbTexSampler2DHandle        = glGetUniformLocation(vbShaderProgramID, "texSampler2D");

        // Retrieve handler for projection matrix shader uniform variable:
        vbProjectionMatrixHandle    = glGetUniformLocation(vbShaderProgramID, "projectionMatrix");
        
        // Retrieve handler for projection matrix shader uniform variable:
        vbTouchLocationXHandle      = glGetUniformLocation(vbShaderProgramID, "touchLocation_x");
        
        // Retrieve handler for projection matrix shader uniform variable:
        vbTouchLocationYHandle      = glGetUniformLocation(vbShaderProgramID, "touchLocation_y");
        
        checkGLError("Getting the handles to the shader variables");

        // Set the orthographic matrix
        setOrthoMatrix(-1.0, 1.0, -1.0, 1.0, -1.0, 1.0, vbOrthoProjMatrix);
        
        // Stop using the program
        glUseProgram(0);
        
        vbShadersInitialized = true;
    }
    else
        LOG("Could not initialize video background shader for the effects");
}

////////////////////////////////////////////////////////////////////////////////
// This function adds the values to the vertex, coord and indices variables.
// Essentially it defines a mesh from -1 to 1 in X and Y with 
// vbNumVertexRows rows and vbNumVertexCols columns. Thus, if we were to assign
// vbNumVertexRows=10 and vbNumVertexCols=10 we would have a mesh composed of 
// 100 little squares (notice, however, that we work with triangles so it is 
// actually not composed of 100 squares but of 200 triangles). The example
// below shows 4 triangles composing 2 squares.
//      D---E---F
//      | \ | \ |
//      A---B---C
void 
CreateVideoBackgroundMesh()
{
    // Get the texture and image dimensions from QCAR
    const QCAR::VideoBackgroundTextureInfo texInfo = QCAR::Renderer::getInstance().getVideoBackgroundTextureInfo();
    
    // If there is no image data yet then return;
    if ((texInfo.mImageSize.data[0] == 0)||(texInfo.mImageSize.data[1] == 0)) return;
        
    // These calculate a slope for the texture coords
    float uRatio = ((float)texInfo.mImageSize.data[0]/(float)texInfo.mTextureSize.data[0]);
    float vRatio = ((float)texInfo.mImageSize.data[1]/(float)texInfo.mTextureSize.data[1]);
    float uSlope = uRatio/(vbNumVertexCols-1);
    float vSlope = vRatio/(vbNumVertexRows-1);
       
    // These calculate a slope for the vertex values in this case we have a span of 2, from -1 to 1
    float totalSpan = 2.0f;
    float colSlope  = totalSpan/(vbNumVertexCols-1);
    float rowSlope  = totalSpan/(vbNumVertexRows-1);
    
    // Some helper variables
    int currentIndexPosition    = 0; 
    int currentVertexPosition   = 0;
    int currentCoordPosition    = 0;
    int currentVertexIndex      = 0;
    
    for (int j = 0; j<vbNumVertexRows; j++)
    {
        for (int i = 0; i<vbNumVertexCols; i++)
        {
            // We populate the mesh with a regular grid
            vbOrthoQuadVertices[currentVertexPosition   /*X*/] = ((colSlope*i)-(totalSpan/2.0f));   // We subtract this because the values range from -totalSpan/2 to totalSpan/2
            vbOrthoQuadVertices[currentVertexPosition+1 /*Y*/] = ((rowSlope*j)-(totalSpan/2.0f));
            vbOrthoQuadVertices[currentVertexPosition+2 /*Z*/] = 0.0f;                              // It is all a flat polygon orthogonal to the view vector
            
            // We also populate its associated texture coordinate
            vbOrthoQuadTexCoords[currentCoordPosition   /*U*/] = uSlope*i;
            vbOrthoQuadTexCoords[currentCoordPosition+1 /*V*/] = vRatio - (vSlope*j);

            // Now we populate the triangles that compose the mesh
            // First triangle is the upper right of the vertex
            if (j<vbNumVertexRows-1)
            {
                if (i<vbNumVertexCols-1) // In the example above this would make triangles ABD and BCE
                {
                    vbOrthoQuadIndices[currentIndexPosition  ] = currentVertexIndex;
                    vbOrthoQuadIndices[currentIndexPosition+1] = currentVertexIndex+1;
                    vbOrthoQuadIndices[currentIndexPosition+2] = currentVertexIndex+vbNumVertexCols;
                    currentIndexPosition += 3;
                }
                if (i>0) // In the example above this would make triangles BED and CFE
                {
                    vbOrthoQuadIndices[currentIndexPosition  ] = currentVertexIndex;
                    vbOrthoQuadIndices[currentIndexPosition+1] = currentVertexIndex+vbNumVertexCols;
                    vbOrthoQuadIndices[currentIndexPosition+2] = currentVertexIndex+vbNumVertexCols-1;
                    currentIndexPosition += 3;
                }
            }
            currentVertexPosition   += 3; // Three values per vertex (x,y,z)
            currentCoordPosition    += 2; // Two texture coordinates per vertex (u,v)
            currentVertexIndex      += 1; // Vertex index increased by one
        }
    }
    vbMeshInitialized = true;
}

JNIEXPORT int JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_getOpenGlEsVersionNative(JNIEnv *, jobject)
{
    return 2;
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_setActivityPortraitMode(JNIEnv *, jobject, jboolean isPortrait)
{
    isActivityInPortraitMode = isPortrait;
}


JNIEXPORT int JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_initTracker(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_initTracker");
    
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
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_deinitTracker(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_deinitTracker");

    // Deinit the image tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    trackerManager.deinitTracker(QCAR::Tracker::IMAGE_TRACKER);
}


JNIEXPORT int JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_loadTrackerData(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_loadTrackerData");
    
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

    // Create the data sets:
    dataSetTarmac = imageTracker->createDataSet();
    if (dataSetTarmac == 0)
    {
        LOG("Failed to create a new tracking data.");
        return 0;
    }

    // Load the data sets:
    if (!dataSetTarmac->load("Tarmac.xml", QCAR::DataSet::STORAGE_APPRESOURCE))
    {
        LOG("Failed to load data set.");
        return 0;
    }

    // Activate the data set:
    if (!imageTracker->activateDataSet(dataSetTarmac))
    {
        LOG("Failed to activate data set.");
        return 0;
    }

    LOG("Successfully loaded and activated data set.");
    return 1;
}


JNIEXPORT int JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_destroyTrackerData(JNIEnv *, jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_destroyTrackerData");

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

    if (dataSetTarmac != 0)
    {
        if (!imageTracker->deactivateDataSet(dataSetTarmac))
        {
            LOG("Failed to destroy the tracking data set Tarmac because the data set "
                "could not be deactivated.");
            return 0;
        }

        if (!imageTracker->destroyDataSet(dataSetTarmac))
        {
            LOG("Failed to destroy the tracking data set Tarmac.");
            return 0;
        }

        LOG("Successfully destroyed the data set Tarmac.");
        dataSetTarmac = 0;
        return 1;
    }

    LOG("No tracker data set to destroy.");
    return 0;
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_onQCARInitializedNative(JNIEnv *, jobject)
{
    // Comment in to enable tracking of up to 2 targets simultaneously and
    // split the work over multiple frames:
    // QCAR::setHint(QCAR::HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 2);
    // QCAR::setHint(QCAR::HINT_IMAGE_TARGET_MULTI_FRAME_ENABLED, 1);
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccessRenderer_renderFrame(JNIEnv *, jobject)
{
    // Clear color and depth buffer 
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Get the state from QCAR and mark the beginning of a rendering section
    QCAR::State state = QCAR::Renderer::getInstance().begin();

    /////////////////////////////////////////////////////////////////
    // This section renders the video background with a 
    // custom shader defined in Shaders.h
    QCAR::Renderer::getInstance().bindVideoBackground(0);

    glDisable(GL_DEPTH_TEST);
    glDisable(GL_CULL_FACE);

    // Load the shader and upload the vertex/texcoord/index data
    glViewport(viewportPosition_x, viewportPosition_y, viewportSize_x, viewportSize_y);
    
    // We need a finer mesh for this background 
    // We have to create it here because it will request the texture info of the video background
    if (!vbMeshInitialized)
        CreateVideoBackgroundMesh();
   
    glUseProgram(vbShaderProgramID);
    glVertexAttribPointer(vbVertexPositionHandle, 3, GL_FLOAT, GL_FALSE, 0, vbOrthoQuadVertices);
    glVertexAttribPointer(vbVertexTexCoordHandle, 2, GL_FLOAT, GL_FALSE, 0, vbOrthoQuadTexCoords);
    glUniform1i(vbTexSampler2DHandle, 0);
    glUniformMatrix4fv(vbProjectionMatrixHandle, 1, GL_FALSE, &vbOrthoProjMatrix[0]);
    glUniform1f(vbTouchLocationXHandle, (touchLocation_x*2.0)-1.0);
    glUniform1f(vbTouchLocationYHandle, (2.0-(touchLocation_y*2.0))-1.0);
    
    // Render the video background with the custom shader
    // First, we enable the vertex arrays
    glEnableVertexAttribArray(vbVertexPositionHandle);
    glEnableVertexAttribArray(vbVertexTexCoordHandle);
    
    // Then, we issue the render call
    glDrawElements(GL_TRIANGLES, vbNumIndices, GL_UNSIGNED_BYTE, vbOrthoQuadIndices);
    
    // Finally, we disable the vertex arrays
    glDisableVertexAttribArray(vbVertexPositionHandle);
    glDisableVertexAttribArray(vbVertexTexCoordHandle);

    // Wrap up this rendering    
    glUseProgram(0);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, 0);
    
    SampleUtils::checkGlError("Rendering of the background failed");
    //
    /////////////////////////////////////////////////////////////////
    
    
    
    /////////////////////////////////////////////////////////////////
    // The following section is similar to image targets
    // we still render the teapot on top of the targets
    
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);
    
    // Did we find any trackables this frame?
    for(int tIdx = 0; tIdx < state.getNumActiveTrackables(); tIdx++)
    {
        // Get the trackable:
        const QCAR::Trackable* trackable = state.getActiveTrackable(tIdx);
        QCAR::Matrix44F modelViewMatrix =
            QCAR::Tool::convertPose2GLMatrix(trackable->getPose());        

        // Choose the texture based on the target name:
        int textureIndex = 0;

        const Texture* const thisTexture = textures[textureIndex];

        QCAR::Matrix44F modelViewProjection;

        SampleUtils::translatePoseMatrix(0.0f, 0.0f, kObjectScale,
                                         &modelViewMatrix.data[0]);
        SampleUtils::scalePoseMatrix(kObjectScale, kObjectScale, kObjectScale,
                                     &modelViewMatrix.data[0]);
        SampleUtils::multiplyMatrix(&projectionMatrix.data[0],
                                    &modelViewMatrix.data[0] ,
                                    &modelViewProjection.data[0]);

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
                           (GLfloat*)&modelViewProjection.data[0] );
        glDrawElements(GL_TRIANGLES, NUM_TEAPOT_OBJECT_INDEX, GL_UNSIGNED_SHORT,
                       (const GLvoid*) &teapotIndices[0]);

        SampleUtils::checkGlError("BackgroundTextureAccess renderFrame");

    }

    glDisable(GL_DEPTH_TEST);

    glDisableVertexAttribArray(vertexHandle);
    glDisableVertexAttribArray(normalHandle);
    glDisableVertexAttribArray(textureCoordHandle);
    
    //
    /////////////////////////////////////////////////////////////////

    // It is always important to tell the QCAR Renderer 
    // that we are finished
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

    LOG("Configure Video Background : Video (%d,%d), Screen (%d,%d), mSize (%d,%d)", videoMode.mWidth, videoMode.mHeight, screenWidth, screenHeight, config.mSize.data[0], config.mSize.data[1]);
    
    viewportPosition_x = ((screenWidth - config.mSize.data[0]) / 2) + config.mPosition.data[0];
    viewportPosition_y =  (((int)(screenHeight - config.mSize.data[1])) / (int) 2) +config.mPosition.data[1];
    viewportSize_x = config.mSize.data[0];
    viewportSize_y = config.mSize.data[1];

    // Set the config:
    QCAR::Renderer::getInstance().setVideoBackgroundConfig(config);
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_initApplicationNative(
                            JNIEnv* env, jobject obj, jint width, jint height)
{
    LOG("Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_initApplicationNative");
    
    // Store screen dimensions
    screenWidth = width;
    screenHeight = height;
        
    // Handle to the activity class:
    jclass activityClass = env->GetObjectClass(obj);

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
        "getTexture", "(I)Lcom/qualcomm/QCARSamples/BackgroundTextureAccess/Texture;");

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
    LOG("Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_initApplicationNative finished");
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_deinitApplicationNative(
                                                        JNIEnv* env, jobject obj)
{
    LOG("Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_deinitApplicationNative");

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
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_startCamera(JNIEnv *,
                                                                         jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_startCamera");

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

    // Uncomment to enable flash
    //if(QCAR::CameraDevice::getInstance().setFlashTorchMode(true))
    //	LOG("IMAGE TARGETS : enabled torch");

    // Uncomment to enable infinity focus mode, or any other supported focus mode
    // See CameraDevice.h for supported focus modes
    //if(QCAR::CameraDevice::getInstance().setFocusMode(QCAR::CameraDevice::FOCUS_MODE_INFINITY))
    //	LOG("IMAGE TARGETS : enabled infinity focus");

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
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_stopCamera(JNIEnv *,
                                                                   jobject)
{
    LOG("Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_stopCamera");

    // Stop the tracker:
    QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
    QCAR::Tracker* imageTracker = trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER);
    if(imageTracker != 0)
        imageTracker->stop();
    
    QCAR::CameraDevice::getInstance().stop();
    QCAR::CameraDevice::getInstance().deinit();
    
    // Force the reinitialization of shaders when resume
    vbShadersInitialized = false;
    vbMeshInitialized = false;
}

JNIEXPORT jboolean JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_toggleFlash(JNIEnv*, jobject, jboolean flash)
{
    return QCAR::CameraDevice::getInstance().setFlashTorchMode((flash == JNI_TRUE)) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_autofocus(JNIEnv*, jobject)
{
    return QCAR::CameraDevice::getInstance().startAutoFocus()?JNI_TRUE:JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_setFocusMode(JNIEnv*, jobject, jint mode)
{
    return QCAR::CameraDevice::getInstance().setFocusMode(mode)?JNI_TRUE:JNI_FALSE;
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccessRenderer_initRendering(
                                                    JNIEnv* env, jobject obj)
{
    LOG("Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccessRenderer_initRendering");

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

    if (!vbShadersInitialized)
        VBSetupShaderProgram();
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccessRenderer_updateRendering(
                        JNIEnv* env, jobject obj, jint width, jint height)
{
    LOG("Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccessRenderer_updateRendering");

    // Update screen dimensions
    screenWidth     = width;
    screenHeight    = height;

    // Reconfigure the video background
    configureVideoBackground();
}


JNIEXPORT void JNICALL
Java_com_qualcomm_QCARSamples_BackgroundTextureAccess_BackgroundTextureAccess_nativeTouchEvent(JNIEnv *, jobject, jfloat x, jfloat y)
{
    // We will use these for the Loupe effect
    // We need to check whether they are in the [-1,1] range. This is 
    // because some devices can send touch events beyond the screen
    // real estate. The value -100.0 is simply used as a flag
    // for the shader to ignore the position
    if ((x >= -1.0)&&(x <= 1.0))    touchLocation_x = x;
    else                            touchLocation_x = -100.0;
    if ((y >= -1.0)&&(y <= 1.0))    touchLocation_y = y;
    else                            touchLocation_y = -100.0;
}



#ifdef __cplusplus
}
#endif
