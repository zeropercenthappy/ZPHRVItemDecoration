# ZPHRVItemDecoration

## 分割线

### LinearLayoutManager

#### 用法

- 全包裹:

  ```java
  LinearLayoutManagerDivider divider = new LinearLayoutManagerDivider(color, dividerWidth);
  // 如果RecyclerView有HeaderView或FooterView（不需要绘制分割线）
  // 按下面的方法添加进Divider中（不会影响UI，仅用于计算）
  divider.addHeaderView(headerView);
  divider.addFooterView(footerView);
  //
  rv.addItemDecoration(divider);
  ```

  ![LinearLayoutManagerFullwrap](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/LinearLayoutManagerDividerFullWrap.png)

- 非全包裹:

  ```java
  LinearLayoutManagerDivider divider = new LinearLayoutManagerDivider(color, dividerWidth, false);
  // 如果RecyclerView有HeaderView或FooterView（不需要绘制分割线）
  // 按下面的方法添加进Divider中（不会影响UI，仅用于计算）
  divider.addHeaderView(headerView);
  divider.addFooterView(footerView);
  //
  rv.addItemDecoration(divider);
  ```

  ![LinearLayoutManager](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/LinearLayoutManagerDivider.png)

### GridLayoutManager

#### 用法 (当前仅支持`Vertical`方向的`GridLayoutManager`绘制分割线)

- Full Wrap:

  ```java
  GridLayoutManagerDivider divider = new GridLayoutManagerDivider(color, dividerWidth);
  // 如果RecyclerView有HeaderView或FooterView（不需要绘制分割线）
  // 按下面的方法添加进Divider中（不会影响UI，仅用于计算）
  divider.addHeaderView(headerView);
  divider.addFooterView(footerView);
  //
  rv.addItemDecoration(divider);
  ```

  ![GridLayoutManager](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/GridLayoutManagerDividerFullWrap.png)

- Not Full Wrap:

  ```java
  GridLayoutManagerDivider divider = new GridLayoutManagerDivider(color, dividerWidth, false);
  // 如果RecyclerView有HeaderView或FooterView（不需要绘制分割线）
  // 按下面的方法添加进Divider中（不会影响UI，仅用于计算）
  divider.addHeaderView(headerView);
  divider.addFooterView(footerView);
  //
  rv.addItemDecoration(divider);
  ```

  ![GridLayoutManager](https://github.com/zeropercenthappy/ZPHRVItemDecoration/blob/master/screenshots/GridLayoutManagerDivider.png)

## 下载

### 步骤 1.

添加以下配置到项目根目录位置的build.gradle：

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### 步骤 2.

在Module目录下的build.gradle文件内添加依赖：

```groovy
dependencies {
	implementation 'com.github.zeropercenthappy:ZPHRVItemDecoration:1.0.8'
}
```

## 更新日志

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

