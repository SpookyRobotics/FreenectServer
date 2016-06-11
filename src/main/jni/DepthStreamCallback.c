#include <jni.h>
#include "DepthStreamCallback.h"

JNIEXPORT void JNICALL Java_com_spookybox_freenect_DepthStreamCallback_depthCallback (JNIEnv * env, jobject thiz, jobject byteBuffer) {
    jclass thisClass = (*env)->GetObjectClass(env, thiz);

   // Get the Method ID for method "callback", which takes no arg and return void
   jmethodID midCallBack = (*env)->GetMethodID(env, thisClass, "array", "()V");
}

JNIEXPORT void JNICALL Java_com_spookybox_freenect_DepthStreamCallback_depthCallbackArray (JNIEnv * env, jobject thiz, jbytearray bytes) {
    depth_cb(bytes);
}

void depth_cb(void *v_depth)
{
	int i;
	uint16_t *depth = (uint16_t*)v_depth;

	for (i=0; i<640*480; i++) {
		int pval = t_gamma[depth[i]];
		int lb = pval & 0xff;
		switch (pval>>8) {
			case 0:
				depth_mid[3*i+0] = 255;
				depth_mid[3*i+1] = 255-lb;
				depth_mid[3*i+2] = 255-lb;
				break;
			case 1:
				depth_mid[3*i+0] = 255;
				depth_mid[3*i+1] = lb;
				depth_mid[3*i+2] = 0;
				break;
			case 2:
				depth_mid[3*i+0] = 255-lb;
				depth_mid[3*i+1] = 255;
				depth_mid[3*i+2] = 0;
				break;
			case 3:
				depth_mid[3*i+0] = 0;
				depth_mid[3*i+1] = 255;
				depth_mid[3*i+2] = lb;
				break;
			case 4:
				depth_mid[3*i+0] = 0;
				depth_mid[3*i+1] = 255-lb;
				depth_mid[3*i+2] = 255;
				break;
			case 5:
				depth_mid[3*i+0] = 0;
				depth_mid[3*i+1] = 0;
				depth_mid[3*i+2] = 255-lb;
				break;
			default:
				depth_mid[3*i+0] = 0;
				depth_mid[3*i+1] = 0;
				depth_mid[3*i+2] = 0;
				break;
		}
	}
}