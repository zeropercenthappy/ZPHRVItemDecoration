# ZPHRVItemDecoration
ItemDecoration for RecycleView with LinearLayoutManager or vertical GridLayoutmanager

---

## Screenshot：
### LinearLayoutManager
![LinearLayout](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/device-2017-12-27-100128.png)
### LinearLayoutManager（Full wrap)
![LinearLayoutFullWrap](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/device-2017-12-27-100205.png)
### GridLayoutManager
![GridLayout](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/device-2017-12-27-100219.png)
### GridLayoutManager(Full wrap)
![GridLayoutFullWrap](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/device-2017-12-27-100232.png)

---

## Method:
+ ### LinearLayoutManager

1. without decoration padding
```java
NormalLLRVDecoration decoration = new NormalLLRVDecoration(this, 10, R.color.colorAccent);
recycleview.addItemDecoration(decoration);
```
2. with decoration padding
```java
NormalLLRVDecoration decoration = new NormalLLRVDecoration(this, 10, 5, R.color.colorAccent);
recycleview.addItemDecoration(decoration);
```
+ ### LinearLayoutManager（Full wrap)
1. without decoration padding
```java
FullLLRVDecoration decoration = new FullLLRVDecoration(this, 10, R.color.colorAccent);
recycleview.addItemDecoration(decoration);
```
2. with decoration padding
```java
FullLLRVDecoration decoration = new FullLLRVDecoration(this, 10, 5, R.color.colorAccent);
recycleview.addItemDecoration(decoration);
```
+ ### GridLayoutManager
```java
NormalVerGLRVDecoration decoration = new NormalVerGLRVDecoration(this, 10, R.color.colorAccent);
recycleview.addItemDecoration(decoration);
```
+ ### GridLayoutManager(Full wrap)
```java
FullVerGLRVDecoration decoration = new FullVerGLRVDecoration(this, 10, R.color.colorAccent);
recycleview.addItemDecoration(decoration);
```
---

## Use:
### Step 1. Add it in your root build.gradle at the end of repositories:
```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
### Step 2. Add the dependency
````
    dependencies {
            compile 'com.github.zeropercenthappy:ZPHRVItemDecoration:1.0.3'
    }
````

## Change log
### 1.0.3
Fix build error
### 1.0.2
Support RecycleView set padding now.
