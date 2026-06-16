inherit cmake pure-cap-kheaders purecap-sysroot

SUMMARY = "A message passing based allocator"
DESCRIPTION = "snmalloc is a research allocator, which implements a message passing based design."
HOMEPAGE = "https://github.com/microsoft/snmalloc"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b98fddd052bb2f5ddbcdbd417ffb26a8"

TOOLCHAIN = "${MORELLO_TOOLCHAIN}"

PACKAGES += " ${PN}-shim ${PN}-test ${PN}-minimal "
PROVIDES += " ${PN}-minimal ${PN}-test ${PN}-shim "

DEPENDS += "virtual/llvm-morello-librt"

RDEPENDS:${PN} += " llvm-morello-librt-dev "
RDEPENDS:${PN}-minimal += " ${PN} "
RDEPENDS:${PN}-shim += " ${PN} "
RDEPENDS:${PN}-dev += " ${PN} llvm-morello "
RDEPENDS:${PN}-test += " ${PN}-dev ${PN}-minimal ${PN}-shim ${PN}-dbg "

SRC_URI = " \
    git://github.com/microsoft/snmalloc.git;protocol=https;branch=main \
    file://0001-pal-linux-add-CHERI-provenance-and-headers.patch \
    file://0002-pal-port-to-cherilinux.patch \
    file://0003-memory-add-backup-rlim64_t-define.patch \
    file://0004-test-cheri-amend-header-inclusion.patch \
    file://0005-test-cheri-fix-alloc-config.patch \
    file://0006-threadalloc-redef-atext-impl.patch \
    file://0007-tests-cheri.cc-remove-outdated-flags.patch \
    file://0008-cheri-test-add-debug-messages.patch \
    file://run-tests.sh \
    "
SRCREV = "cd2b19b9f1c8db1df7d97a97ed6660cc16abd6de"
SRC_URI[sha256sum] = "5e9db0d6a250c4c3e7a5b1d61bf6966fa67a1ad5d4aa050e779d0aa594055116"

S = "${WORKDIR}/git"

DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

CXX_INC_FLAGS = " \
    -I${S}/src/snmalloc/pal/ \
    -isystem ${STAGING_DIR_TARGET}${includedir}/c++/v1 \
    -isystem ${STAGING_DIR_TARGET}${includedir} \
    -include ${STAGING_DIR_NATIVE}/usr/lib/clang/15.0.0/include/cheriintrin.h \
    -isystem ${STAGING_DIR_NATIVE}/usr/lib/clang/15.0.0/include/ \
    -isystem ${STAGING_INCDIR} \
    "

LLVM_INC_FLAGS="-isystem /iceotope/meta-morello-distro/build/tmp-soc/sysroots-components/x86_64/llvm-morello-native/usr/include"
LLVM_FLAGS="${CC_PURECAP_FLAGS} ${CXX_INC_FLAGS}  -Xclang -morello-vararg=new -DLIBCXX_HAS_MUSL_LIBC=ON"
LLVM_CPP_FLAGS="${LLVM_FLAGS} -D_GNU_SOURCE -D__STDC_CONSTANT_MACROS -D__STDC_FORMAT_MACROS -D__STDC_LIMIT_MACROS"
LLVM_C_FLAGS="${LLVM_CPP_FLAGS}"
LLVM_CXX_FLAGS="${LLVM_C_FLAGS} -std=c++14 -stdlib=libc++ -rtlib=compiler-rt"

LD_PURECAP_FLAGS:append = "-B${STAGING_DIR_TARGET}${libdir} -lc++abi -lunwind -lc"

PURECAP_LIBDIR = "${STAGING_DIR_TARGET}${libdir}"
PURECAP_INCDIR = "${STAGING_DIR_TARGET}${incdir}"

EXTRA_OECMAKE += " \
    --debug-trycompile \
    -DCMAKE_BUILD_TYPE=Release \
    -DSNMALLOC_BUILD_TESTING=1 \
    -DCMAKE_ASM_COMPILER_TARGET="${GLOBAL_LIB_TRIPLE}" \
    -DCMAKE_C_COMPILER_TARGET="${GLOBAL_LIB_TRIPLE}" \
    -DCMAKE_CXX_COMPILER_TARGET="${GLOBAL_LIB_TRIPLE}" \
    -DCMAKE_LINKER='${LLVM_PATH}/ld.lld' \
    -DCMAKE_ASM_FLAGS='${CC_PURECAP_FLAGS}' \
    -DCMAKE_INSTALL_RPATH='$ORIGIN/../lib:${PURECAP_LIBDIR}' \
    -DCMAKE_SYSROOT='${STAGING_DIR_TARGET}${PURECAP_SYSROOT_DIR}' \
    -DCMAKE_C_FLAGS="${LLVM_C_FLAGS}" \
    -DCMAKE_CXX_FLAGS="${LLVM_CXX_FLAGS}" \
    -DCMAKE_EXE_LINKER_FLAGS="${LLVM_LD_FLAGS} ${LD_PURECAP_FLAGS}" \
    -DCMAKE_SHARED_LINKER_FLAGS="${LLVM_LD_FLAGS} ${LD_PURECAP_FLAGS}" \
    "

do_install:append () {
    TEST_BIN_PATH=${bindir}/${PN}
    SRC_PATH=${datadir}/src/${PN}
    LIB_PATH=${libdir}
    
    install -d  ${D}${TEST_BIN_PATH}
    cp ${B}/perf-* ${D}${TEST_BIN_PATH}
    cp ${B}/func-* ${D}${TEST_BIN_PATH}

    install -m 744 ${WORKDIR}/run-tests.sh ${D}${bindir}
    sed -i "s|@TEST_BIN_PATH@|${TEST_BIN_PATH}|g" ${D}${bindir}/run-tests.sh

    # install -d ${D}/${SRC_PATH}
    # cp -a ${S}/. ${D}${SRC_PATH}

    install -d ${D}/${LIBPATH}
    cp -r ${B}/*.so  ${D}${LIB_PATH}
    cp -r ${B}/*.a  ${D}${LIB_PATH}

    bbwarn "PN ${PN}"
    bbwarn "datadir ${datadir}"
    bbwarn "-src FILES ${datadir}/src/${PN}"
    bbwarn "ls ${datadir}/src/${PN} $(ls ${D}${datadir}/src/${PN} | head -n5)"
}

FILES:${PN} = "\
    ${includedir}/snmalloc \
    "

# FILES:${PN}-src = "\
#     /usr/lib/aarch64-linux-musl_purecap/usr/share/src/snmalloc/** \
#     "

# CMake files
FILES:${PN}-dev = "\
    ${datadir}/snmalloc \
    ${libdir}/libsnmallocshim-static.a \
    ${libdir}/libsnmalloc-new-override.a \
    "

FILES:${PN}-shim = "\
    ${libdir}/libsnmallocshim.so \
    "

FILES:${PN}-minimal = "\
    ${libdir}/libsnmalloc-minimal.so \
    "

FILES:${PN}-test = "\
    ${bindir}/run-tests.sh \
    ${bindir}/snmalloc/func-* \
    ${bindir}/snmalloc/perf-* \
    ${libdir}/libsnmallocshim-checks-memcpy-only.so \
    ${libdir}/libsnmallocshim-checks.so \
    "
