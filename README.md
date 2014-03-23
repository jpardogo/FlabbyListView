FlabbyListView
==============

Android library to display a ListView which cells are not rigid but flabby and react to ListView scroll.

A video example of this library is on this [youtube video][1].

<p align="center">
    <img height=393 width=200 src="https://raw.githubusercontent.com/jpardogo/FlabbyListView/master/art/flappy_shot.png"/>
</p>

Usage
-----

1.Place the `FlabbyListView` on your layout:

```xml
    <com.jpardogo.android.flabbylistview.lib.FlabbyListView
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        android:id="@android:id/list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

2.Populate it with items which xml layout is wrap by a `FlabbyLayout`:


```xml
    <!--Notice that this view extends from <FrameLayout/>.-->
    <com.jpardogo.android.flabbylistview.lib.FlabbyLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/apk/tools"
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:orientation="vertical">

        <!--Your content-->

    </FlabbyLayout>
```
3.Set the color of each item. It needs to be set on the getView method of your adapter calling `setFlabbyColor` from `FlabbyLayout`:

```java
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int color = Color.argb(255, mRandomizer.nextInt(256), mRandomizer.nextInt(256), mRandomizer.nextInt(256));
        ((FlabbyLayout)convertView).setFlabbyColor(color);
        holder.text.setText(getItem(position));
        return convertView;
    }
```

Developed By
--------------------

Javier Pardo de Santayana Gómez - <jpardogo@gmail.com>

<a href="https://twitter.com/jpardogo">
  <img alt="Follow me on Twitter"
       src="https://raw.github.com/jpardogo/ListBuddies/master/art/ic_twitter.png" />
</a>
<a href="https://plus.google.com/u/0/+JavierPardo/posts">
  <img alt="Follow me on Google+"
       src="https://raw.github.com/jpardogo/ListBuddies/master/art/ic_google+.png" />
</a>
<a href="http://www.linkedin.com/profile/view?id=155395637">
  <img alt="Follow me on LinkedIn"
       src="https://raw.github.com/jpardogo/ListBuddies/master/art/ic_linkedin.png" />

License
-----------

    Copyright 2013 Javier Pardo de Santayana Gómez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: https://www.youtube.com/watch?v=yN6oO4dBgW8
