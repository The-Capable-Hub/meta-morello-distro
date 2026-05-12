inherit pkgconfig autotools gettext texinfo gtk-doc lib_package purecap-sysroot

MORELLO_SRC = "poky/meta/recipes-extended/libidn/libidn2_2.3.2.bb"

SUMMARY = "Internationalized Domain Name support library"
DESCRIPTION = "Implementation of the Stringprep, Punycode and IDNA specifications defined by the IETF Internationalized Domain Names (IDN) working group."
HOMEPAGE = "http://www.gnu.org/software/libidn/"
SECTION = "libs"
LICENSE = "(GPL-2.0-or-later | LGPL-3.0-only) & GPL-3.0-or-later & Unicode-DFS-2016"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d834ea7d480438ada04e5d846152395 \
                    file://COPYING.LESSERv3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                    file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.unicode;md5=684cf5f7e3fded3546679424528261a9 \
                    file://src/idn2.c;endline=16;md5=afc1531bda991ba6338e33a7eff758a0 \
                    file://lib/idn2.h.in;endline=27;md5=f88d218005a5c45b68a83cecb5bd7f26"

SRC_URI = "${GNU_MIRROR}/libidn/libidn2-${PV}.tar.gz"
SRC_URI[sha256sum] = "4c21a791b610b9519b9d0e12b8097bf2f359b12f8dd92647611a929e6bfd7d64"

TOOLCHAIN  = "${MORELLO_TOOLCHAIN}"

S = "${WORKDIR}/libidn2-${PV}"

DEPENDS = "virtual/libiconv libunistring-morello"

DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

EXTRA_OECONF += "--disable-rpath \
                 --with-libunistring-prefix=${STAGING_EXECPREFIXDIR} \
                 "

do_install() {
    install -d ${D}
    oe_runmake DESTDIR=${D} install
}

do_install:append() {
	# Need to remove any duplicate whitespace too for reproducibility
	sed -i -e 's|-L${STAGING_DIR_TARGET}${libdir}||' -e 's/  */ /g' ${D}${libdir}/pkgconfig/libidn2.pc
}

do_install:append() {
	rm -rf ${D}${datadir}/emacs
}

do_install:append() {
	${READELF_COMMAND} ${D}${libdir}/libidn2.so >  ${D}${PURECAP_DEBUGDIR}/libidn2.so.readelf
}

LICENSE:${PN} = "(GPL-2.0-or-later | LGPL-3.0-only) & Unicode-DFS-2016"
LICENSE:${PN}-bin = "GPL-3.0-or-later"
