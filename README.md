# Forward Navigation
![Platform](https://img.shields.io/badge/platform-android-green.svg)
![API](https://img.shields.io/badge/API-19%2B-green.svg?style=flat)
[![License](https://img.shields.io/badge/License-Apache&#032;2.0-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

Forward Navigation for Android provides easy navigation for an ordered sequence of screens.
Forward Navigation is based on ViewStub.
[Read Material Design documentation to learn more.][material]

![Forward Navigation Example](http://robertkoziej.com/android/forwardnavigation_thumb.png "Forward Navigation Example")

### Gradle
    dependencies {
    	implementation 'com.robertkoziej:forwardnavigation:1.0.3'
    }
### Usage
#### Layout

    <com.robertkoziej.forwardnavigiation.ForwardNavigation
    	android:id="@+id/fwNav"
    	android:layout_width="match_parent"
    	android:layout_height="72dp"
    	app:fn_viewCount="two"
    	app:fn_currentView="0"
    	app:fn_navigationIndicatorFocusedColor="@color/colorPrimaryDark"
    	app:fn_navigationIndicatorDefaultColor="@color/colorPrimaryLight"
    	app:fn_buttonTextColor="@color/colorPrimaryDark"
    	app:fn_lastViewNextButtonText="@string/next_activity"
    	app:fn_inflatedId="@+id/forward_navigation_container"
    	app:layout_constraintBottom_toBottomOf="parent"
    	app:layout_constraintEnd_toEndOf="parent"
    	app:layout_constraintStart_toStartOf="parent"/>

#### Attributes

|  Attribute Name | Default Value  | Description |
| ------------ | ------------ | ------------ |
| fn_viewCount  | ViewCount.TWO (2) | The number of indicators. Range: 2-7  |
| fn_currentView  | 0 | The index of current screen (activity or fragment). Indexed from 0  |
| fn_navigationIndicatorActiveColor  | @color/fn_navigationIndicatorActiveColor | The color of active (current) view indicator  |
| fn_navigationIndicatorDefaultColor  | @color/fn_navigationIndicatorDefaultColor | The color of not active views indicators  |
| fn_buttonTextColor  | @color/fn_buttonTextColor  |  The color of button text
| fn_lastViewNextButtonText  | null |  The nextButton text value that appears when current view is the last view  |
| fn_backgroundColor  | @color/fn_backgroundColor  |   The color of all elements that form the background. |


#### Activity/Fragment
1. Initialize ForwardNavigation
2. Set attributes (optional)
3. Call inflate()
4. Set listener

	   forwardNavigation = findViewById(R.id.fwNav);
	   forwardNavigation.inflate();
	   forwardNavigation.setOnClickListener(new ForwardNavigation.OnClickListener() {
	   	@Override
		public void onNextButtonClicked() {
			Log.d(TAG, "onNextButtonClicked");
		}

		@Override
		public void onBackButtonClicked() {
			Log.d(TAG, "onBackButtonClicked");
		}
	   });

#### Applying new attributes (optional)
1. Set attributes
2. Call inflate()

	   forwardNavigation.setNavigationAttrs(ForwardNavigation.ViewCount.SEVEN, currentView);
	   forwardNavigation.inflate();
	
[material]: https://material.io/design/navigation/understanding-navigation.html#forward-navigation "material.io"