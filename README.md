
<p align="center"><strong>线程</strong></p>


* ##### 线程切换
```java
    // 工作线程
        TS.postIo(new Runnable() {
            @Override
            public void run() {

            }
        });
        // 主线程
        MH.postToMain(new Runnable() {
            @Override
            public void run() {

            }
        });
```
<br>

在项目根目录的 build.gradle 添加仓库

```groovy
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

在 module 的 build.gradle 添加依赖

```groovy
implementation 'com.github.cjfsss:Thread:0.0.2'
```

<br>


## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
