require recipes-core/util-linux/util-linux.inc

inherit autotools gettext pkgconfig pure-cap-kheaders purecap-sysroot gtk-doc

PV = "2.39.3"
MORELLO_SRC = "poky/meta/recipes-core/util-linux/util-linux_${PV}.bb"

SUMMARY = "A suite of basic system administration utilities"

TOOLCHAIN = "${MORELLO_TOOLCHAIN}"

S = "${WORKDIR}/util-linux-${PV}"
EXTRA_OECONF += "--disable-all-programs --enable-libuuid --enable-libblkid --enable-libmount"
LICENSE = "BSD-3-Clause"

DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

do_install() {
	install_dir="${D}"
	install -d ${install_dir}
	oe_runmake DESTDIR=${install_dir} install
	rm -rf ${install_dir}${datadir} ${install_dir}${bindir} ${install_dir}${base_bindir} \
	${install_dir}${sbindir} ${install_dir}${base_sbindir} ${install_dir}${exec_prefix}/sbin
}
