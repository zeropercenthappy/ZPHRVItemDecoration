# ZPHRVItemDecoration [简体中文](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/README_CN.md)

## Divider

### LinearLayoutManager

#### Usage

- Full Wrap:

  ```java
  LinearLayoutManagerDivider divider = new LinearLayoutManagerDivider(color, dividerWidth);
  // if you have header view or footer view.
  // it will not influence layout, only use for calculate.
  divider.addHeaderView(headerView);
  divider.addFooterView(footerView);
  //
  rv.addItemDecoration(divider);
  ```

  ![LinearLayoutManagerFullwrap](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/LinearLayoutManagerDividerFullWrap.png)

- Not Full Wrap:

  ```java
  LinearLayoutManagerDivider divider = new LinearLayoutManagerDivider(color, dividerWidth, false);
  // if you have header view or footer view.
  // it will not influence layout, only use for calculate.
  divider.addHeaderView(headerView);
  divider.addFooterView(footerView);
  //
  rv.addItemDecoration(divider);
  ```

  ![LinearLayoutManager](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/LinearLayoutManagerDivider.png)

### GridLayoutManager

#### Usage (Current version only support `Vertical` `GridLayoutManager`)

- Full Wrap:

  ```java
  GridLayoutManagerDivider divider = new GridLayoutManagerDivider(color, dividerWidth, true);
  // if you want to set a different size of divider:
  GridLayoutManagerDivider divider = new GridLayoutManagerDivider(color, horizontalDividerHeight, horizontalDividerHeight, true);
  // if you have header view or footer view.
  // it will not influence layout, only use for calculate.
  divider.addHeaderView(headerView);
  divider.addFooterView(footerView);
  //
  rv.addItemDecoration(divider);
  ```

  ![GridLayoutManager](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/GridLayoutManagerDividerFullWrap.png)

- Not Full Wrap:

  ```java
  GridLayoutManagerDivider divider = new GridLayoutManagerDivider(color, dividerWidth, false);
  // if you want to set a different size of divider:
  GridLayoutManagerDivider divider = new GridLayoutManagerDivider(color, horizontalDividerHeight, verticalDividerWidth, false);
  // if you have header view or footer view.
  // it will not influence layout, only use for calculate.
  divider.addHeaderView(headerView);
  divider.addFooterView(footerView);
  //
  rv.addItemDecoration(divider);
  ```

  ![GridLayoutManager](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/GridLayoutManagerDivider.png)

## Download

### Step 1.

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### Step 2.

Add the dependency in your module's build.gradle:

```groovy
dependencies {
    // For android compat version
    implementation 'com.github.zeropercenthappy:ZPHRVItemDecoration:1.0.9'
    // For androidX version
    implementation 'com.github.zeropercenthappy:ZPHRVItemDecoration:1.1.0'
}
```

## Change log

- 1.1.0

  Migrate to AndroidX.

- 1.0.9

  Support different size of horizontal and vertical divider for GridLayoutManager.

- 1.0.8

  fix draw way of not full wrap style.

- 1.0.7

  Use kotlin to rewrite, rewrite all calculate logic, support to set header view and footer view.

- 1.0.6

  Downgrade minSdkVersion to 14

- 1.0.4

  Add sources jar

- 1.0.3

  Fix build error

- 1.0.2

  Support RecycleView set padding now.

