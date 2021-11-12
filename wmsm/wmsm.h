#pragma once

#include <jni.h>
#include "pch.h"

using namespace winrt;
using namespace Windows::Media::Control;
using namespace Windows::Foundation::Collections;

void refreshSessions();

void refreshSession(JNIEnv *, jobject, jstring);

void updateSessions(const GlobalSystemMediaTransportControlsSessionManager &);

void updateSession(const GlobalSystemMediaTransportControlsSession &);

void onMediaPropertiesChanged(const GlobalSystemMediaTransportControlsSession &,
                              const MediaPropertiesChangedEventArgs &);

void onPlaybackInfoChanged(const GlobalSystemMediaTransportControlsSession &,
                           const PlaybackInfoChangedEventArgs &);
