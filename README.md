# ZPHRVItemDecoration
ItemDecoration for RecycleView with LinearLayoutManager or vertical GridLayoutmanager

Screenshot：

![LinearLayout](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/device-2017-12-27-100128.png)
![LinearLayoutFullWrap](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/device-2017-12-27-100205.png)

![GridLayout](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/device-2017-12-27-100219.png)
![GridLayoutFullWrap](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/device-2017-12-27-100232.png)

Use:

Step 1. Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        compile 'com.github.zeropercenthappy:ZPHRVItemDecoration:1.0.2'
	}

<b>Change log</b><br><br>
<b>1.0.2</b><br>
Support RecycleView set padding now.
