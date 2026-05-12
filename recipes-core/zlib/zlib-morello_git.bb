inherit ptest purecap-sysroot

MORELLO_SRC = "poky/meta/recipes-core/zlib/zlib_1.3.1.bb"

SUMMARY     = "Zlib Compression Library"
DESCRIPTION = "Zlib is a general-purpose, patent-free, lossless data compression \
library which is used by many different programs."

HOMEPAGE         = "http://zlib.net/"
SECTION          = "libs"
LICENSE          = "Zlib"
LIC_FILES_CHKSUM = "file://zlib.h;beginline=6;endline=23;md5=5377232268e952e9ef63bc555f7aa6c0"

TOOLCHAIN = "${MORELLO_TOOLCHAIN}"

SRC_URI = "git://github.com/madler/zlib;protocol=https;branch=${SRCBRANCH} \
           file://run-ptest \
           file://CVE-2026-27171.patch \
           file://0001-configure-Pass-LDFLAGS-to-link-tests.patch \
           "
# 1.3.1
SRCREV    = "51b7f2abdade71cd9bb0e7a373ef2610ec6f9daf"
SRCBRANCH = "master"

CFLAGS += "-D_REENTRANT"

RDEPENDS:${PN}-ptest += "make"

S = "${WORKDIR}/git"

DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

do_configure() {
  LDCONFIG=true ${S}/configure --prefix=${prefix} --libdir=${libdir} --uname=GNU
}
# do_configure[cleandirs] += "${B}"

do_compile() {
  oe_runmake shared
}

do_install() {
	oe_runmake DESTDIR=${D} install
}

do_install_ptest() {
  install -d ${D}${PURECAP_SYSROOT_DIR}${PTEST_PATH}
  install ${B}/examplesh ${D}${PURECAP_SYSROOT_DIR}${PTEST_PATH}
}

# Adding 'CVE_PRODUCT' to avoid false detection of CVEs
CVE_PRODUCT = "zlib:zlib gnu:zlib"

CVE_STATUS[CVE-2026-22184] = "not-applicable-config: vulnerable file is not compiled"
