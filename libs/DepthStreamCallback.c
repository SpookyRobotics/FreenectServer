#include <stdint.h>
#include <stdlib.h>
#include <math.h>
#include <stdio.h>
#include "DepthStreamCallback.h"

void depth_cb(uint16_t* t_gamma, uint16_t *depth, uint8_t * rgbBuffer);

/*
 * Class:     com_spookybox_freenect_DepthStreamCallback
 * Method:    depthCallback
 * Signature: (Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
 */
JNIEXPORT void JNICALL Java_com_spookybox_freenect_DepthStreamCallback_depthCallback
    (JNIEnv * env, jclass thiz, jobject gammaBuffer, jobject depthStreamBuffer, jobject rgbBuffer){
    uint16_t* t_gamma = (uint16_t*) env->GetDirectBufferAddress(gammaBuffer);
    uint16_t* depthStream = (uint16_t*) env->GetDirectBufferAddress(depthStreamBuffer);
    uint8_t * rgbStream = (uint8_t*) env->GetDirectBufferAddress(rgbBuffer);
    if(t_gamma == NULL) {
        printf("Invalid t_gamma"); fflush(stdout);
        return;
    }
    else if(depthSt
    ream == NULL) {
        printf("Invalid depth stream."); fflush(stdout);
        return;
    }
    else if(rgbStream == NULL) {
        printf("Invalid rgb stream."); fflush(stdout);
        return;
    }
    depth_cb(t_gamma, depthStream, rgbStream);
}

/*
 * Class:     com_spookybox_freenect_DepthStreamCallback
 * Method:    initGammaArray
 * Signature: ()Ljava/nio/ByteBuffer;
 */
JNIEXPORT     printf("Converting"); fflush(stdout);
void JNICALL Java_com_spookybox_freenect_DepthStreamCallback_initGammaArray(JNIEnv * env, jclass thiz, jobject buffer) {
    uint16_t* t_gamma = (uint16_t*) env->GetDirectBufferAddress(buffer);
    int i;
    for (i=0; i<2048; i++) {
      float v = i/2048.0;
      v = powf(v, 3)* 6;
      t_gamma[i] = v*6*256;
      //printf("%hu ",t_gamma[i]);
    }
    //printf("\n");
    //fflush(stdout);
}

void depth_cb(uint16_t* t_gamma, uint16_t *depth, uint8_t * rgbBuffer){
	int i;
	for (i=0; i<640*480; i++) {
	    //printf("pixel %d - depthValue %hu\n", i, depth[i]); fflush(stdout);
	    uint16_t depthValue = depth[i];
	    if(depthValue > 2048){
	        printf("Read invalid depth value -> %hu", depthValue); fflush(stdout);
	        depthValue = 0;
	    }
		int pval = t_gamma[depthValue];
		int lb = pval & 0xff;
		switch (pval>>8) {
			case 0:
				rgbBuffer[3*i+0] = 255;
				rgbBuffer[3*i+1] = 255-lb;
				rgbBuffer[3*i+2] = 255-lb;
				break;
			case 1:
				rgbBuffer[3*i+0] = 255;
				rgbBuffer[3*i+1] = lb;
				rgbBuffer[3*i+2] = 0;
				break;
			case 2:
				rgbBuffer[3*i+0] = 255-lb;
				rgbBuffer[3*i+1] = 255;
				rgbBuffer[3*i+2] = 0;
				break;
			case 3:
				rgbBuffer[3*i+0] = 0;
				rgbBuffer[3*i+1] = 255;
				rgbBuffer[3*i+2] = lb;
				break;
			case 4:
				rgbBuffer[3*i+0] = 0;
				rgbBuffer[3*i+1] = 255-lb;
				rgbBuffer[3*i+2] = 255;
				break;
			case 5:
				rgbBuffer[3*i+0] = 0;
				rgbBuffer[3*i+1] = 0;
				rgbBuffer[3*i+2] = 255-lb;
				break;
			default:
				rgbBuffer[3*i+0] = 0;
				rgbBuffer[3*i+1] = 0;
				rgbBuffer[3*i+2] = 0;
				break;
		}
		//printf("Wrote pixel\n"); fflush(stdout);
	}
}