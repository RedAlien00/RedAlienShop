#include <jni.h>
#include <android/log.h>
#include <dirent.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <stdbool.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define TAG "Detect"
#define PROC_MAPS "/proc/self/maps"

// 코드 참고 - 본인
// Android 9, 14에서 모두 동작하는 것으로 확인
// 기존에 사용하던 fopen( /data/local/tmp) 코드는 접근이 불가함 but access()를 사용하면 체크 가능
JNIEXPORT jboolean JNICALL
Java_com_RedAlien_RedAlienShop_Helper_DoDetect_nativeIsFridaBinary(JNIEnv *env, jobject thiz){
    char filename_template[] = "/data/local/tmp/frida-server-%s-android-%s";
    char filename[100];
    const char *arch[] = { "arm", "arm64", "x86", "x86_64" };
    const int arch_size = sizeof(arch) / sizeof(arch[0]);
    const char *versions[] = { // 2024년에 나온 버전 시리즈들
            "16.5.6",
            "16.5.5",
            "16.5.4",
            "16.5.3",
            "16.5.2",
            "16.5.1",
            "16.5.0",
            "16.4.10",
            "16.4.9",
            "16.4.8",
            "16.4.7",
            "16.4.6",
            "16.4.5",
            "16.4.4",
            "16.4.3",
            "16.4.2",
            "16.4.1",
            "16.4.0",
            "16.3.3",
            "16.3.2",
            "16.3.1",
            "16.3.0",
            "16.2.5",
            "16.2.4",
            "16.2.3",
            "16.2.2",
            "16.2.1",
            "16.2.0",
            "16.1.11"
    };
    const int versions_size = sizeof(versions) / sizeof(versions[0]);

    for (int i = 0; i < arch_size; ++i) {
        const char *current_arch = arch[i];
        for (int j = 0; j < versions_size; ++j) {
            const char *current_version = versions[j];
            sprintf(filename, filename_template,current_version, current_arch);

            if (access(filename, F_OK) == 0) {
                __android_log_print(ANDROID_LOG_WARN, TAG, "Check! : %s", filename);
                return true;
            }
        }
    }
    __android_log_print(ANDROID_LOG_INFO, TAG, "Failed to Access");
    return false;
}

// 코드 참고 - OWASP Uncrackable 3
// Android 9, 14에서 모두 동작하는 것으로 확인
// Frida를 사용하여 앱을 실행했을 때 감지하는 코드
JNIEXPORT jboolean JNICALL
Java_com_RedAlien_RedAlienShop_Helper_DoDetect_nativeIsFridaMapped(JNIEnv *env, jobject thiz) {
    char buffer[512];
    FILE *fp = fopen(PROC_MAPS, "r");

    if (fp == NULL) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "Failed to open : %s", PROC_MAPS);
        return false;
    }
    while (fgets(buffer, sizeof(buffer), fp )){
        if(strstr(buffer, "frida")){
            __android_log_print(ANDROID_LOG_WARN, TAG, "Frida library has been mapped !!");
            fclose(fp);
            return true;
        }
    }
    __android_log_print(ANDROID_LOG_INFO, TAG, "Frida library is not mapped");
    return false;

}

// 코드 참고 - 라온 화이트햇
// Android 9, 14에서 모두 동작하는 것으로 확인
// Frida Server를 실행시킨 상태라면 감지하는 코드
// 우회 방법 : Frida Server 실행 시, -l 0.0.0.0:9999 와 같이 포트를 변경하면 됨
JNIEXPORT jboolean JNICALL
Java_com_RedAlien_RedAlienShop_Helper_DoDetect_nativeIsFridaServerListening(JNIEnv *env,jobject thiz) {
    struct sockaddr_in sa;
    memset(&sa, 0, sizeof(sa)); // 구조체 초기화
    struct sockaddr so;

    inet_aton("127.0.0.1", &(sa.sin_addr)); // IP 주소를 바이너리 형태로 변환하여 구조체에 저장
    sa.sin_port = htons(27042); // Little Endian > Big Endian
    sa.sin_family = AF_INET;    // Address Family IPv4

    // 소켓 초기화( IPv4, TCP )
    int sock = socket(AF_INET, SOCK_STREAM, 0);

    if(connect(sock, (struct sockaddr*)&sa, sizeof(sa)) == -1){
        __android_log_print(ANDROID_LOG_INFO, TAG, "Frida Server is not Listening");
        close(sock);
        return false;
    }
    __android_log_print(ANDROID_LOG_WARN, TAG, "Frida Server is Listening !");
    close(sock);
    return true;
}

JNIEXPORT void JNICALL
Java_com_RedAlien_RedAlienShop_Helper_DoDetect_goodbye(JNIEnv *env, jobject thiz) {
    _exit(0);
}
