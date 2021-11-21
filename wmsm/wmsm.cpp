#pragma comment(lib, "windowsapp")

#include <iostream>
#include <string>
#include "wmsm.h"

static const int METHOD_LENGTH = 6;
static const JNINativeMethod methods[] = {
        {"refreshSessions", "()V",                   (void *) refreshSessions},
        {"refreshSession",  "(Ljava/lang/String;)V", (void *) refreshSession},
        {"play",            "(Ljava/lang/String;)V", (void *) play},
        {"pause",           "(Ljava/lang/String;)V", (void *) pause},
        {"skipToPrevious",  "(Ljava/lang/String;)V", (void *) skipToPrevious},
        {"skipToNext",      "(Ljava/lang/String;)V", (void *) skipToNext},
};

JavaVM *jvm = nullptr;
jclass clazz = nullptr;

std::vector<GlobalSystemMediaTransportControlsSession> sessions;
GlobalSystemMediaTransportControlsSessionManager sessionManager{nullptr};

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *javaVM, void *reversed) {
    jvm = javaVM;
    JNIEnv *jniEnv = nullptr;
    if (jvm->GetEnv(reinterpret_cast<void **>(&jniEnv), JNI_VERSION_1_8) != JNI_OK) {
        return JNI_ERR;
    }
    try {
        clazz = jniEnv->FindClass("com/xhhold/plugin/WinRTMediaSessionManager");
        jniEnv->RegisterNatives(clazz, methods, METHOD_LENGTH);
        clazz = reinterpret_cast<jclass>(jniEnv->NewGlobalRef(clazz));
        sessionManager = GlobalSystemMediaTransportControlsSessionManager::RequestAsync().get();
        sessionManager.SessionsChanged(onSessionsChanged);
        // updateSessions(sessionManager);
    }
    catch (...) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_8;
}

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
}


void refreshSessions() {
    updateSessions(sessionManager);
}

void refreshSession(JNIEnv *env, jobject thiz, jstring id) {
    auto str = env->GetStringUTFChars(id, nullptr);
    std::string idStr = str;
    for (auto &session: sessions) {
        if (winrt::to_string(session.SourceAppUserModelId()) == idStr) {
            updateSession(session);
        }
    }
    env->ReleaseStringUTFChars(id, str);
}

void play(JNIEnv *env, jobject thiz, jstring id) {
    auto str = env->GetStringUTFChars(id, nullptr);
    std::string idStr = str;
    for (auto &session: sessions) {
        if (winrt::to_string(session.SourceAppUserModelId()) == idStr) {
            session.TryPlayAsync();
        }
    }
    env->ReleaseStringUTFChars(id, str);
}

void pause(JNIEnv *env, jobject thiz, jstring id) {
    auto str = env->GetStringUTFChars(id, nullptr);
    std::string idStr = str;
    for (auto &session: sessions) {
        if (winrt::to_string(session.SourceAppUserModelId()) == idStr) {
            session.TryPauseAsync();
        }
    }
    env->ReleaseStringUTFChars(id, str);
}

void skipToPrevious(JNIEnv *env, jobject thiz, jstring id) {
    auto str = env->GetStringUTFChars(id, nullptr);
    std::string idStr = str;
    for (auto &session: sessions) {
        if (winrt::to_string(session.SourceAppUserModelId()) == idStr) {
            session.TrySkipPreviousAsync();
        }
    }
    env->ReleaseStringUTFChars(id, str);
}

void skipToNext(JNIEnv *env, jobject thiz, jstring id) {
    auto str = env->GetStringUTFChars(id, nullptr);
    std::string idStr = str;
    for (auto &session: sessions) {
        if (winrt::to_string(session.SourceAppUserModelId()) == idStr) {
            session.TrySkipNextAsync();
        }
    }
    env->ReleaseStringUTFChars(id, str);
}

void updateSessions(const GlobalSystemMediaTransportControlsSessionManager &manager) {
    auto result = manager.GetSessions();
    sessions.clear();
    for (auto session: result) {
        auto id = winrt::to_string(session.SourceAppUserModelId());
        session.MediaPropertiesChanged(onMediaPropertiesChanged);
        session.PlaybackInfoChanged(onPlaybackInfoChanged);
        sessions.push_back(session);
        updateSession(session);
    }
/*
    JNIEnv *jniEnv = nullptr;
    jvm->AttachCurrentThread(reinterpret_cast<void **>(&jniEnv), nullptr);
    auto onSessionsMethodId = jniEnv->GetStaticMethodID(clazz, "onSessions", "([Ljava/lang/String;)V");
    auto stringClass = jniEnv->FindClass("java/lang/String");
    auto ids = jniEnv->NewObjectArray(static_cast<long>(result.Size()), stringClass, nullptr);

    int i = 0;
    for (auto session: result) {
        auto id = jniEnv->NewStringUTF(winrt::to_string(session.SourceAppUserModelId()).c_str());
        jniEnv->SetObjectArrayElement(ids, i++, id);
    }
    jniEnv->CallStaticVoidMethod(clazz, onSessionsMethodId, ids);
    for (int j = 0; j < result.Size(); j++) {
        auto obj = jniEnv->GetObjectArrayElement(ids, i);
        jniEnv->DeleteLocalRef(obj);
    }
    jniEnv->DeleteLocalRef(stringClass);
    jniEnv->DeleteLocalRef(stringClass);*/
}

void updateSession(const GlobalSystemMediaTransportControlsSession &session) {
    auto id = winrt::to_string(session.SourceAppUserModelId());
    auto properties = session.TryGetMediaPropertiesAsync().get();
    if (properties == nullptr)
        return;

    auto info = session.GetPlaybackInfo();
    if (info == nullptr)
        return;

    auto title = winrt::to_string(properties.Title());
    auto artist = winrt::to_string(properties.Artist());
    auto album = winrt::to_string(properties.AlbumTitle());
    auto thumbnail = properties.Thumbnail();
    auto playStatus = info.PlaybackStatus();
    JNIEnv *jniEnv = nullptr;
    jvm->AttachCurrentThread(reinterpret_cast<void **>(&jniEnv), nullptr);
    auto onSessionMethodId = jniEnv->GetStaticMethodID(clazz, "onSession",
                                                       "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V");

    auto jniId = jniEnv->NewStringUTF(id.c_str());
    auto jniTitle = jniEnv->NewStringUTF(title.c_str());
    auto jniArtist = jniEnv->NewStringUTF(artist.c_str());
    auto jniAlbum = jniEnv->NewStringUTF(album.c_str());

    jniEnv->CallStaticVoidMethod(clazz, onSessionMethodId,
                                 jniId,
                                 jniTitle,
                                 jniArtist,
                                 jniAlbum,
                                 static_cast<int>(playStatus));

    jniEnv->DeleteLocalRef(jniId);
    jniEnv->DeleteLocalRef(jniTitle);
    jniEnv->DeleteLocalRef(jniArtist);
    jniEnv->DeleteLocalRef(jniAlbum);
}

void onMediaPropertiesChanged(const GlobalSystemMediaTransportControlsSession &session,
                              const MediaPropertiesChangedEventArgs &args) {
    updateSession(session);
}

void onPlaybackInfoChanged(const GlobalSystemMediaTransportControlsSession &session,
                           const PlaybackInfoChangedEventArgs &args) {
    updateSession(session);
}

void onSessionsChanged(const GlobalSystemMediaTransportControlsSessionManager &manager,
                       const SessionsChangedEventArgs &args) {
    updateSessions(manager);
}
