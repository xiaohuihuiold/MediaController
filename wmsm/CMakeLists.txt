cmake_minimum_required(VERSION 3.20)
set(CMAKE_CXX_STANDARD 17)
project(wmsm)

include(CTest)
enable_testing()

include_directories(
        $ENV{JAVA_HOME}/include
        $ENV{JAVA_HOME}/include/win32
        )
add_library(wmsm SHARED wmsm.cpp pch.cpp wmsm.h)
