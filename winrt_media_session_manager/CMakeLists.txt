cmake_minimum_required(VERSION 3.0.0)
project(winrt_media_session_manager VERSION 0.1.0)
set(CMAKE_CXX_STANDARD 17)

include(CTest)
enable_testing()

add_library(winrt_media_session_manager SHARED media_session_manager.cpp pch.c)

set(CPACK_PROJECT_NAME ${PROJECT_NAME})
set(CPACK_PROJECT_VERSION ${PROJECT_VERSION})
include(CPack)
