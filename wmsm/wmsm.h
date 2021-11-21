#pragma once

#include <jni.h>
#include "pch.h"

using namespace winrt;
using namespace Windows::Media::Control;
using namespace Windows::Foundation::Collections;

void refreshSessions();

void refreshSession(JNIEnv *, jobject, jstring);

void play(JNIEnv *, jobject, jstring);

void pause(JNIEnv *, jobject, jstring);

void skipToPrevious(JNIEnv *, jobject, jstring);

void skipToNext(JNIEnv *, jobject, jstring);

void updateSessions(const GlobalSystemMediaTransportControlsSessionManager &);

void updateSession(const GlobalSystemMediaTransportControlsSession &);

void onMediaPropertiesChanged(const GlobalSystemMediaTransportControlsSession &,
                              const MediaPropertiesChangedEventArgs &);

void onPlaybackInfoChanged(const GlobalSystemMediaTransportControlsSession &,
                           const PlaybackInfoChangedEventArgs &);

void onSessionsChanged(const GlobalSystemMediaTransportControlsSessionManager &,
                       const SessionsChangedEventArgs &);