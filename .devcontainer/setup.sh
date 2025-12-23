#!/bin/bash

echo "=== Lightning Browser Auto Setup ==="

# -----------------------------
# 1. 安装 SDKMAN
# -----------------------------
echo ">>> Installing SDKMAN"
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# -----------------------------
# 2. 安装 JDK 17
# -----------------------------
echo ">>> Installing JDK 17"
sdk install java 17.0.10-ms
sdk default java 17.0.10-ms

echo "Java version:"
java -version

# -----------------------------
# 3. 安装 Android SDK（命令行工具）
# -----------------------------
echo ">>> Installing Android SDK"
sudo apt-get update -y
sudo apt-get install -y unzip wget

mkdir -p $HOME/android-sdk
cd $HOME/android-sdk

wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdtools.zip
unzip cmdtools.zip

mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/

export ANDROID_HOME=$HOME/android-sdk
echo "export ANDROID_HOME=$HOME/android-sdk" >> ~/.bashrc

export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH
echo "export PATH=\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools:\$PATH" >> ~/.bashrc

# -----------------------------
# 4. 安装 SDK 组件
# -----------------------------
echo ">>> Installing Android SDK components"
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# -----------------------------
# 5. 写入 local.properties
# -----------------------------
cd /workspaces/Lightning-Browser
echo "sdk.dir=$HOME/android-sdk" > local.properties

# -----------------------------
# 6. 清理 Gradle 缓存
# -----------------------------
echo ">>> Cleaning Gradle caches"
./gradlew --stop >/dev/null 2>&1
rm -rf ~/.gradle/daemon ~/.gradle/caches ~/.gradle/configuration-cache

# -----------------------------
# 7. 自动构建 Lite + Plus
# -----------------------------
echo ">>> Building Lightning Lite"
./gradlew assembleLightningLiteRelease --no-daemon

echo ">>> Building Lightning Plus"
./gradlew assembleLightningPlusRelease --no-daemon

echo "=== Lightning Browser Auto Setup Complete ==="
