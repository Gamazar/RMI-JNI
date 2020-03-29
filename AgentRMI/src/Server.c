#include <jni.h>
#include "Agent.h"
#include <sys/utsname.h>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int GetLocalTime(){
	time_t now = time(NULL);
	time(&now);
	struct tm *time = localtime(&now);
	int i = (time->tm_hour*3600) + (time->tm_min*60) +(time->tm_sec);

	return i;
}

JNIEXPORT void JNICALL Java_Agent_C_1GetLocalTime
  (JNIEnv * env, jobject callingObject, jobject timeObject){
	//printf("SERVER jni here....\n");

	jclass thisClass = (*env)->GetObjectClass(env,timeObject);

	jfieldID time_fid = (*env)->GetFieldID(env,thisClass,"time","I");
	jfieldID valid_fid = (*env)->GetFieldID(env,thisClass,"valid","C");
	if(time_fid == NULL){
		(*env)->SetIntField(env,timeObject,time_fid,-1);
		(*env)->SetIntField(env,timeObject,valid_fid,'0');
		return;
	}
	else{
		(*env)->SetIntField(env,timeObject,time_fid,GetLocalTime());
		(*env)->SetIntField(env,timeObject,valid_fid,'1');
	}

	return; //timeObject;
}

JNIEXPORT void JNICALL Java_Agent_C_1GetVersion(JNIEnv * env, jobject callingObject, jobject version){

	struct utsname OS_Name;
	uname(&OS_Name);
	jclass thisClass = (*env)->GetObjectClass(env,version);
	jfieldID fidOS = (*env)->GetFieldID(env,thisClass,"version","Ljava/lang/String;");

	if(fidOS == NULL){

	}
	else{
		//char *buf = OS_Name.sysname;
		//char * temp = (char *)malloc(sizeof(OS_Name.sysname));
		//strcpy(temp,buf);
		jstring newVersion = (*env)->NewStringUTF(env,OS_Name.sysname);
		(*env)->SetObjectField(env,version,fidOS,newVersion);
	}
	return;
}
