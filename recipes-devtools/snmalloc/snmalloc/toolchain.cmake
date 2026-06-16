# CMake system name must be something like "Linux".
# This is important for cross-compiling.

set( CMAKE_SYSTEM_NAME Linux )
set( CMAKE_SYSTEM_PROCESSOR aarch64 )
set( CMAKE_C_COMPILER /usr/bin/clang )
set( CMAKE_CXX_COMPILER /usr/bin/clang++ )
set( CMAKE_C_COMPILER_LAUNCHER  )
set( CMAKE_CXX_COMPILER_LAUNCHER  )
set( CMAKE_ASM_COMPILER /usr/bin/clang )
find_program( CMAKE_AR /usr/bin/llvm-ar DOC "Archiver" REQUIRED )

set( CMAKE_ASM_COMPILER_TARGET "aarch64-unknown-linux-musl_purecap" )
set( CMAKE_C_COMPILER_TARGET "aarch64-unknown-linux-musl_purecap" )
set( CMAKE_CXX_COMPILER_TARGET "aarch64-unknown-linux-musl_purecap" )

set( COMMON_FLAGS "-march=morello \
-mabi=purecap \
-Werror=implicit-function-declaration \
-Werror=format \
-Werror=undefined-internal \
-Werror=incompatible-pointer-types \
-Werror=cheri-capability-misuse \
-Werror=cheri-bitwise-operations \
-isystem /usr/lib/aarch64-linux-musl_purecap/usr/src/linux-headers-morello/include" CACHE STRING "COMMON_FLAGS" )

set( ASM_FLAGS_EXTRA " ${COMMON_FLAGS} " CACHE STRING "ASM_FLAGS_EXTRA" )

set( CFLAGS_EXTRA "  ${ASM_FLAGS_EXTRA} \
 -I/usr/lib/aarch64-linux-musl_purecap/usr/src/snmalloc/src/snmalloc/pal \
 -isystem /usr/lib/aarch64-linux-musl_purecap/usr/include/c++/v1 \
 -isystem /usr/lib/aarch64-linux-musl_purecap/usr/include \
 -include /usr/lib/clang/15.0.0/include/cheriintrin.h \
 -isystem /usr/lib/clang/15.0.0/include/ \
 -isystem /usr/lib/aarch64-linux-musl_purecap/usr/include \
 -Xclang \
 -morello-vararg=new \
 -DLIBCXX_HAS_MUSL_LIBC=ON \
 -D_GNU_SOURCE \
 -D__STDC_CONSTANT_MACROS \
 -D__STDC_FORMAT_MACROS \
 -D__STDC_LIMIT_MACROS" CACHE STRING "CFLAGS_EXTRA")

set(CXX_FLAGS_EXTRA " \
 ${CFLAGS_EXTRA} \
 -std=c++14 \
 -stdlib=libc++ \
 -rtlib=compiler-rt" CACHE STRING "CXX_FLAGS_EXTRA")

set( LINKER_FLAGS_EXTRA " \
 -L/usr/lib/aarch64-linux-musl_purecap/usr/lib \
 -rtlib=compiler-rt \
 -Wl,-rpath=/usr/lib/aarch64-linux-musl_purecap/usr/lib \
 -B/usr/lib/aarch64-linux-musl_purecap/usr/lib \
 -lc++abi \
 -lunwind \
 -lc" CACHE STRING "LINKER_FLAGS_EXTRA" )

 set( CMAKE_ASM_FLAGS " ${ASM_FLAGS_EXTRA} -mcpu=neoverse-n1+crypto -mbranch-protection=standard -O2 -pipe -g -feliminate-unused-debug-types -L/usr/lib" CACHE STRING "ASM FLAGS" )
set( CMAKE_C_FLAGS " ${CFLAGS_EXTRA} -mcpu=neoverse-n1+crypto -mbranch-protection=standard -O2 -pipe -g -feliminate-unused-debug-types -L/usr/lib " CACHE STRING "CFLAGS" )
set( CMAKE_CXX_FLAGS " ${CXX_FLAGS_EXTRA} -mcpu=neoverse-n1+crypto -mbranch-protection=standard -O2 -pipe -g -feliminate-unused-debug-types -fvisibility-inlines-hidden -L/usr/lib/aarch64-linux-musl_purecap/usr/lib" CACHE STRING "CXXFLAGS" )
set( CMAKE_C_FLAGS_RELEASE "-DNDEBUG" CACHE STRING "Additional CFLAGS for release" )
set( CMAKE_CXX_FLAGS_RELEASE "-DNDEBUG" CACHE STRING "Additional CXXFLAGS for release" )
set( CMAKE_ASM_FLAGS_RELEASE "-DNDEBUG" CACHE STRING "Additional ASM FLAGS for release" )
set( CMAKE_C_LINK_FLAGS     " ${LINKER_FLAGS_EXTRA} -mcpu=neoverse-n1+crypto -mbranch-protection=standard -Wl,-O1 -Wl,--hash-style=gnu -Wl,--as-needed -L/usr/lib -rtlib=compiler-rt -Wl,-rpath=/usr/lib/aarch64-linux-musl_purecap/usr/lib -B/usr/lib/aarch64-linux-musl_purecap/usr/lib -lc++abi -lunwind -lc" CACHE STRING "LDFLAGS" )
set( CMAKE_CXX_LINK_FLAGS   " ${LINKER_FLAGS_EXTRA} -mcpu=neoverse-n1+crypto -mbranch-protection=standard -O2 -pipe -g -feliminate-unused-debug-types -fvisibility-inlines-hidden -Wl,-O1 -Wl,--hash-style=gnu -Wl,--as-needed -L/usr/lib -rtlib=compiler-rt -Wl,-rpath=/usr/lib/aarch64-linux-musl_purecap/usr/lib -B/usr/lib/aarch64-linux-musl_purecap/usr/lib -lc++abi -lunwind -lc" CACHE STRING "LDFLAGS" )
set( CMAKE_SHARED_LINKER_FLAGS "${LINKER_FLAGS_EXTRA}" CACHE STRING "SHARED_LINKER_FLAGS")

set( CMAKE_EXE_LINKER_FLAGS "${LINKER_FLAGS_EXTRA}" CACHE STRING "EXE LINKER FLAGS")

# only search in the paths provided so cmake doesnt pick
# up libraries and tools from the native build machine
set( CMAKE_FIND_ROOT_PATH       /)
set( CMAKE_FIND_ROOT_PATH_MODE_PACKAGE ONLY )
set( CMAKE_FIND_ROOT_PATH_MODE_PROGRAM ONLY )
set( CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY )
set( CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY )
set( CMAKE_PROGRAM_PATH "/" )

set( CMAKE_SYSROOT "/usr/lib/aarch64-linux-musl_purecap" )

# Use qt.conf settings
# set( ENV{QT_CONF_PATH} /meta-morello/meta-morello-distro/build/tmp-soc/work/neoversen1-oe-linux/snmalloc/git/qt.conf )

# We need to set the rpath to the correct directory as cmake does not provide any
# directory as rpath by default
set( CMAKE_INSTALL_RPATH  )

# Use RPATHs relative to build directory for reproducibility
set( CMAKE_BUILD_RPATH_USE_ORIGIN ON )

# Use our cmake modules
list(APPEND CMAKE_MODULE_PATH "/usr/lib/aarch64-linux-musl_purecap/usr/share/cmake/Modules/")

# add for non /usr/lib libdir, e.g. /usr/lib64
set( CMAKE_LIBRARY_PATH /usr/lib/aarch64-linux-musl_purecap/usr/lib /usr/lib/aarch64-linux-musl_purecap/usr/lib)

# add include dir to implicit includes in case it differs from /usr/include
list(APPEND CMAKE_C_IMPLICIT_INCLUDE_DIRECTORIES /usr/lib/aarch64-linux-musl_purecap/usr/include)
list(APPEND CMAKE_CXX_IMPLICIT_INCLUDE_DIRECTORIES /usr/lib/aarch64-linux-musl_purecap/usr/include)

