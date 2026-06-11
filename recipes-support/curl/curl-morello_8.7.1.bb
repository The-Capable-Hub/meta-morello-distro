inherit autotools pkgconfig binconfig multilib_header
inherit purecap-sysroot

MORELLO_SRC = "poky/meta/recipes-support/curl/curl_7.82.0.bb"

SUMMARY = "Command line tool and library for client-side URL transfers"
DESCRIPTION = "It uses URL syntax to transfer data to and from servers. \
curl is a widely used because of its ability to be flexible and complete \
complex tasks. For example, you can use curl for things like user authentication, \
HTTP post, SSL connections, proxy support, FTP uploads, and more!"
HOMEPAGE = "https://curl.se/"
BUGTRACKER = "https://github.com/curl/curl/issues"
SECTION = "console/network"
LICENSE = "curl"
LIC_FILES_CHKSUM = "file://COPYING;md5=eed2e5088e1ac619c9a1c747da291d75"

TOOLCHAIN  = "${MORELLO_TOOLCHAIN}"

SRC_URI = " \
    https://curl.se/download/curl-${PV}.tar.xz \
    file://721941aadf4adf4f6aeb3f4c0ab489bb89610c36.patch \
    file://run-ptest \
    file://disable-tests \
    file://no-test-timeout.patch \
    file://CVE-2024-6197.patch \
    file://CVE-2024-7264-1.patch \
    file://CVE-2024-7264-2.patch \
    file://CVE-2024-8096.patch \
    file://CVE-2024-9681.patch \
    file://CVE-2024-11053-0001.patch \
    file://CVE-2024-11053-0002.patch \
    file://CVE-2024-11053-0003.patch \
    file://CVE-2025-0167.patch \
    file://CVE-2025-9086.patch \
    file://CVE-2025-10148.patch \
    file://CVE-2025-14017.patch \
    file://CVE-2025-14524.patch \
    file://0001-build-enable-Wcast-qual-fix-or-silence-compiler-warn.patch \
    file://CVE-2025-14819.patch \
    file://CVE-2025-15079.patch \
    file://CVE-2025-15224.patch \
"
SRC_URI[sha256sum] = "6fea2aac6a4610fbd0400afb0bcddbe7258a64c63f1f68e5855ebc0c659710cd"

S = "${WORKDIR}/curl-${PV}"

# Curl has used many names over the years...
CVE_PRODUCT = "haxx:curl haxx:libcurl curl:curl curl:libcurl libcurl:libcurl daniel_stenberg:curl"

CVE_STATUS[CVE-2024-32928] = "ignored: CURLOPT_SSL_VERIFYPEER was disabled on google cloud services causing a potential man in the middle attack"
CVE_STATUS[CVE-2025-0725] = "not-applicable-config: gzip decompression of content-encoded HTTP responses with the `CURLOPT_ACCEPT_ENCODING` option, using zlib 1.2.0.3 or older"
CVE_STATUS[CVE-2025-5025] = "${@bb.utils.contains('PACKAGECONFIG', 'openssl', 'not-applicable-config: applicable only with wolfssl','unpatched',d)}"
CVE_STATUS[CVE-2025-10966] = "${@bb.utils.contains('PACKAGECONFIG', 'openssl', 'not-applicable-config: applicable only with wolfssl','unpatched',d)}"

# Entropy source for random PACKAGECONFIG option
RANDOM ?= "/dev/urandom"

PACKAGECONFIG = "openssl proxy random verbose zlib"

DEPENDS += "openssl-morello zlib-morello openldap-morello libidn2-morello"

# 'ares' and 'threaded-resolver' are mutually exclusive
# PACKAGECONFIG[brotli] = "--with-brotli,--without-brotli,brotli"
PACKAGECONFIG[builtinmanual] = "--enable-manual,--disable-manual"
PACKAGECONFIG[dict] = "--enable-dict,--disable-dict,"
PACKAGECONFIG[imap] = "--enable-imap,--disable-imap,"
PACKAGECONFIG[libidn] = "--with-libidn2,--without-libidn2,libidn2-morello"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[ldap] = "--enable-ldap,--disable-ldap,openldap-morello"
PACKAGECONFIG[ldaps] = "--enable-ldaps,--disable-ldaps,openldap-morello"
PACKAGECONFIG[mqtt] = "--enable-mqtt,--disable-mqtt,"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl-morello"
PACKAGECONFIG[pop3] = "--enable-pop3,--disable-pop3,"
PACKAGECONFIG[proxy] = "--enable-proxy,--disable-proxy,"
PACKAGECONFIG[random] = "--with-random=${RANDOM},--without-random"
PACKAGECONFIG[smtp] = "--enable-smtp,--disable-smtp,"
PACKAGECONFIG[verbose] = "--enable-verbose,--disable-verbose"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib-morello"

EXTRA_OECONF = " \
    --disable-manual \
    --enable-threaded-resolver \
    --disable-libcurl-option \
    --disable-ntlm-wb \
    --with-ca-bundle=${sysconfdir}/ssl/certs/ca-certificates.crt \
    --without-libpsl \
    --enable-debug \
    --enable-optimize \
    --disable-curldebug \
"

DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

do_install:append:class-target() {
  # cleanup buildpaths from curl-config
  sed -i \
      -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
      -e 's,--with-libtool-sysroot=${STAGING_DIR_TARGET},,g' \
      -e 's|${DEBUG_PREFIX_MAP}||g' \
      -e 's|${@" ".join(d.getVar("DEBUG_PREFIX_MAP").split())}||g' \
      ${D}${bindir}/curl-config
}

do_install:append() {
  ${READELF_COMMAND} ${D}${libdir}/libcurl.so >  ${D}${PURECAP_DEBUGDIR}/libcurl.so.readelf
}

PACKAGES =+ "lib${BPN}"

FILES:lib${BPN} = "${libdir}/lib*.so.*"
RRECOMMENDS:lib${BPN} += "ca-certificates"

FILES:${PN} += "${datadir}/zsh"

SYSROOT_DIRS += "${bindir}"