require ncurses-morello.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/cheri-patches:"

SRC_URI += "git://github.com/mirror/ncurses;protocol=https;branch=${SRCBRANCH} \
			file://0001-test-ncurses-silence-capability-misuse.patch \
			"

SRCBRANCH = "master"
SRCREV    = "79b9071f2be20a24c7be031655a5638f6032f29f"

S         = "${WORKDIR}/git"

DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

RPROVIDES:${PN} = "ncurses-morello"

RDEPENDS:${PN}        += "bash"
