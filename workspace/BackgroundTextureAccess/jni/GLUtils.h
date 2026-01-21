/*==============================================================================
            Copyright (c) 2011 QUALCOMM Austria Research Center GmbH.
            All Rights Reserved.
            Qualcomm Confidential and Proprietary
==============================================================================*/

#include <GLES/gl.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

/// Error handling macro: execute action 'ACTION' 
/// if error condition 'ERRORCOND' is true.
#define CHECK_ERROR(ERRORCOND, ACTION) if ((ERRORCOND) == AR_TRUE) { ACTION; }

void handleError();

void checkGLError(const char* nOperation);
bool loadShaderFromFile(const char *nFilename, char*& nShaderBuffer);
unsigned int initShaderFromSourceBuffer(unsigned int nShaderType, const char* nSource);
unsigned int createShaderProgramFromBuffers(const char* nVertexShaderBuffer, const char* nFragmentShaderBuffer);
unsigned int createShaderProgramFromFiles(const char* nVertexShaderFile, const char* nFragmentShaderFile);
void setOrthoMatrix(float nLeft, float nRight, float nBottom, float nTop, float nNear, float nFar, float *nProjMatrix);