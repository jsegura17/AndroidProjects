/*==============================================================================
            Copyright (c) 2011 QUALCOMM Austria Research Center GmbH.
            All Rights Reserved.
            Qualcomm Confidential and Proprietary
==============================================================================*/

#include <stdio.h>
#include <stdlib.h>
#include "GLUtils.h"

void 
handleError()
{
    printf("PL error occurred\n");

    // Terminate app
    exit(-1);
}

void
checkGLError(const char* /*nOperation*/)
{
//     for (GLint error = glGetError(); error; error = glGetError())
//         arLogMessage(AR_LOG_LEVEL_INFO, "PLShadersExample", "after %s() glError (0x%x) [%d]", nOperation, error, error);
}


bool
loadShaderFromFile(const char *nFilename, char*& nShaderBuffer)
{
    FILE *file = NULL;
    long length = 0;

    file = fopen(nFilename, "r");

    if (!file)
    {
//         arLogMessage(AR_LOG_LEVEL_ERROR, "PLShadersExample", "cannot read shader file '%s'", nFilename);
  		return false;
    }

    // get file length
    //
    fseek(file, 0, SEEK_END);
    length = ftell(file);
    fseek(file, 0, SEEK_SET);

    // read file content into text buffer
    //
    nShaderBuffer = (char*)calloc(length + 1, 1);
    fread(nShaderBuffer, 1, length, file);
    nShaderBuffer[length] = '\0';
    fclose(file);
    
//     arLogMessage(AR_LOG_LEVEL_INFO, "PLShadersExample", "successfully read shader file '%s'", nFilename);

    return true;
}


unsigned int
initShaderFromSourceBuffer(unsigned int nShaderType, const char* nSource)
{
    GLuint shader = glCreateShader((GLenum)nShaderType);
    if (shader)
    {
        glShaderSource(shader, 1, &nSource, NULL);
        glCompileShader(shader);
        GLint compiled = 0;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
    
        if (!compiled)
        {
            GLint infoLen = 0;
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
            if (infoLen)
            {
                char* buf = (char*) malloc(infoLen);
                if (buf)
                {
                    glGetShaderInfoLog(shader, infoLen, NULL, buf);
//                     arLogMessage(AR_LOG_LEVEL_ERROR, "PLShadersExample", "Could not compile shader %d:%s", nShaderType, buf);
                    free(buf);
                }
                glDeleteShader(shader);
                shader = 0;
            }
        }
    }
    return shader;
}


unsigned int
createShaderProgramFromBuffers(const char* nVertexShaderBuffer, 
                               const char* nFragmentShaderBuffer)
{    
    GLuint vertexShader = 
        initShaderFromSourceBuffer(GL_VERTEX_SHADER, nVertexShaderBuffer);
    if (!vertexShader)
        return 0;

    GLuint fragmentShader = 
        initShaderFromSourceBuffer(GL_FRAGMENT_SHADER, nFragmentShaderBuffer);
    if (!fragmentShader)
        return 0;

    GLuint programID = glCreateProgram();
    if (programID)
    {
        glAttachShader(programID, vertexShader);
        checkGLError("glAttachVertexShader");
        
        glAttachShader(programID, fragmentShader);
        checkGLError("glAttachFragmentShader");
        
        glLinkProgram(programID);
        GLint linkStatus = GL_FALSE;
        glGetProgramiv(programID, GL_LINK_STATUS, &linkStatus);
        
        if (linkStatus != GL_TRUE)
        {
            GLint bufLength = 0;
            glGetProgramiv(programID, GL_INFO_LOG_LENGTH, &bufLength);
            if (bufLength)
            {
                char* buf = (char*) malloc(bufLength);
                if (buf)
                {
                    glGetProgramInfoLog(programID, bufLength, NULL, buf);
//                     arLogMessage(AR_LOG_LEVEL_ERROR, "PLShadersExample", "Could not link program : %s", buf);
                    free(buf);
                }
            }
            glDeleteProgram(programID);
            programID = 0;
        }
    }
    glReleaseShaderCompiler();
    return programID;
}


unsigned int
createShaderProgramFromFiles(const char* nVertexShaderFile, 
                             const char* nFragmentShaderFile)
{
    char* vertexShaderBuffer, *fragmentShaderBuffer;

    if (!loadShaderFromFile(nVertexShaderFile, vertexShaderBuffer))
        return 0;

    if (!loadShaderFromFile(nFragmentShaderFile, fragmentShaderBuffer))
        return 0;
        
    unsigned int programID = 
        createShaderProgramFromBuffers(vertexShaderBuffer, fragmentShaderBuffer);

    delete[]vertexShaderBuffer;        
    delete[]fragmentShaderBuffer;
    
    return programID;
}


void
setOrthoMatrix(float nLeft, float nRight, float nBottom, float nTop, 
    float nNear, float nFar, float *nProjMatrix)
{
    if (!nProjMatrix)
    {
//         arLogMessage(AR_LOG_LEVEL_ERROR, "PLShadersExample", "Orthographic projection matrix pointer is NULL");
        return;
    }       
        
    int i;
    for (i = 0; i < 16; i++)
        nProjMatrix[i] = 0.0f;

    nProjMatrix[0] = 2.0f / (nRight - nLeft);
    nProjMatrix[5] = 2.0f / (nTop - nBottom);
    nProjMatrix[10] = 2.0f / (nNear - nFar);
    nProjMatrix[12] = -(nRight + nLeft) / (nRight - nLeft);
    nProjMatrix[13] = -(nTop + nBottom) / (nTop - nBottom);
    nProjMatrix[14] = (nFar + nNear) / (nFar - nNear);
    nProjMatrix[15] = 1.0f;
}
