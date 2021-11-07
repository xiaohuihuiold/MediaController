#pragma comment(lib, "windowsapp")

#include <iostream>
#include <string>
#include "pch.h"
#include "jni.h"

using namespace winrt;
using namespace Windows::Media::Control;
using namespace Windows::Foundation::Collections;

void refreshSessions();
void refreshSession(JNIEnv *, jobject, jstring);
void updateSessions(GlobalSystemMediaTransportControlsSessionManager);
void updateSession(GlobalSystemMediaTransportControlsSession);
void onMediaPropertiesChanged(GlobalSystemMediaTransportControlsSession,
                              MediaPropertiesChangedEventArgs const &);
void onPlaybackInfoChanged(GlobalSystemMediaTransportControlsSession,
                           PlaybackInfoChangedEventArgs const &);

static const JNINativeMethod methods[] = {
    {"refreshSessions", "()V", (void *)refreshSessions},
    {"refreshSession", "(Ljava/lang/String;)V", (void *)refreshSession},
};

JNIEnv *jniEnv = nullptr;
jclass clazz = nullptr;
GlobalSystemMediaTransportControlsSessionManager sessionManager{nullptr};

void refreshSessions()
{
    updateSessions(sessionManager);
}

void refreshSession(JNIEnv *env, jobject thiz, jstring id)
{
    auto str = env->GetStringUTFChars(id, false);
    std::string idStr = str;
    // if (session == nullptr)
    // {
    //     return;
    // }
    // updateSession(session);
    // env->ReleaseStringUTFChars(id, str);
}

void updateSessions(GlobalSystemMediaTransportControlsSessionManager manager)
{
    auto result = manager.GetSessions();
    // sessionMap.clear();
    for (auto &session : result)
    {
        auto id = winrt::to_string(session.SourceAppUserModelId());
        session.MediaPropertiesChanged(onMediaPropertiesChanged);
        session.PlaybackInfoChanged(onPlaybackInfoChanged);
    }
    auto onSessionsMethodId = jniEnv->GetStaticMethodID(clazz, "onSessions", "([Ljava/lang/String;)V");
    auto ids = jniEnv->NewObjectArray(result.Size(), jniEnv->FindClass("java/lang/String"), nullptr);
    int i = 0;
    // for (auto &session : sessionMap)
    // {
    //     auto id = jniEnv->NewStringUTF(session.first.c_str());
    //     jniEnv->SetObjectArrayElement(ids, i++, id);
    // }
    for (auto &session : result)
    {
        auto id = jniEnv->NewStringUTF(winrt::to_string(session.SourceAppUserModelId()).c_str());
        jniEnv->SetObjectArrayElement(ids, i++, id);
    }
    jniEnv->CallStaticVoidMethod(clazz, onSessionsMethodId, ids);
}

void updateSession(GlobalSystemMediaTransportControlsSession session)
{
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

    auto onSessionsMethodId = jniEnv->GetStaticMethodID(clazz, "onSession", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V");
    jniEnv->CallStaticVoidMethod(clazz, onSessionsMethodId,
                                 jniEnv->NewStringUTF(id.c_str()),
                                 jniEnv->NewStringUTF(title.c_str()),
                                 jniEnv->NewStringUTF(artist.c_str()),
                                 jniEnv->NewStringUTF(album.c_str()),
                                 playStatus);
}

void onMediaPropertiesChanged(GlobalSystemMediaTransportControlsSession session,
                              MediaPropertiesChangedEventArgs const &args)
{
    updateSession(session);
}

void onPlaybackInfoChanged(GlobalSystemMediaTransportControlsSession session,
                           PlaybackInfoChangedEventArgs const &args)
{
    updateSession(session);
}

void onSessionsChanged(GlobalSystemMediaTransportControlsSessionManager manager,
                       SessionsChangedEventArgs const &args)
{
    updateSessions(manager);
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *jvm, void *reversed)
{
    if (jvm->GetEnv(reinterpret_cast<void **>(&jniEnv), JNI_VERSION_1_8) != JNI_OK)
    {
        return JNI_ERR;
    }
    try
    {
        clazz = jniEnv->FindClass("com/xhhold/plugin/WinRTMediaSessionManager");
        jniEnv->RegisterNatives(clazz, methods, 2);
        sessionManager = GlobalSystemMediaTransportControlsSessionManager::RequestAsync().get();
        sessionManager.SessionsChanged(onSessionsChanged);
        updateSessions(sessionManager);
    }
    catch (...)
    {
        return JNI_ERR;
    }
    return JNI_VERSION_1_8;
}

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved)
{
}
