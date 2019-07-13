# ZPHRVItemDecoration

## Divider

- LinearLayoutManager

  - Usage

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

      Screenshot:

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

      Screenshot:

      ![LinearLayoutManager](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/LinearLayoutManagerDivider.png)

- GridLayoutManager

  - Usage `(Current version only support Vertical orientation)`

    - Full Wrap:

      ```java
      GridLayoutManagerDivider divider = new GridLayoutManagerDivider(color, dividerWidth);
      // if you have header view or footer view.
      // it will not influence layout, only use for calculate.
      divider.addHeaderView(headerView);
      divider.addFooterView(footerView);
      //
      rv.addItemDecoration(divider);
      ```

      Screenshot:

      ![GridLayoutManager](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/GridLayoutManagerDividerFullWrap.png)

    - Not Full Wrap:

      ```java
      GridLayoutManagerDivider divider = new GridLayoutManagerDivider(color, dividerWidth, false);
      // if you have header view or footer view.
      // it will not influence layout, only use for calculate.
      divider.addHeaderView(headerView);
      divider.addFooterView(footerView);
      //
      rv.addItemDecoration(divider);
      ```

      Screenshot:

      ![GridLayoutManager](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/GridLayoutManagerDivider.png)

## Use

1. Add it in your root build.gradle at the end of repositories:
	```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	```

2. Add the dependency
	```groovy
	dependencies {
		implementation 'com.github.zeropercenthappy:ZPHRVItemDecoration:1.0.7'
	}
	```

## Change log

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
