#!/bin/bash
BASEDIR=$(dirname $0)
WORKDIR=$(cd $BASEDIR; pwd)

sudo rm -Rf bin
mkdir build

# execute docker run
sudo docker run -v ${WORKDIR}/../i386/build:/build \
                -v ${WORKDIR}/../../:/sources \
                --platform linux/i386 \
                -t totalcross/linux-i386-build bash -c "cmake /sources -G Ninja -DUSE_SKIA=OFF && ninja"