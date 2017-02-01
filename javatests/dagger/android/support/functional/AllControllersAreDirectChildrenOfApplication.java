/*
 * Copyright (C) 2017 The Dagger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dagger.android.support.functional;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasDispatchingActivityInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;
import javax.inject.Inject;

public final class AllControllersAreDirectChildrenOfApplication extends Application
    implements HasDispatchingActivityInjector {
  @Inject DispatchingAndroidInjector<Activity> activityInjector;

  @Override
  public void onCreate() {
    super.onCreate();
    DaggerAllControllersAreDirectChildrenOfApplication_ApplicationComponent.create().inject(this);
  }

  @Override
  public DispatchingAndroidInjector<Activity> activityInjector() {
    return activityInjector;
  }

  @Component(modules = ApplicationComponent.ApplicationModule.class)
  interface ApplicationComponent {
    void inject(AllControllersAreDirectChildrenOfApplication application);

    @Module(
      subcomponents = {
        ActivitySubcomponent.class,
        ParentFragmentSubcomponent.class,
        ChildFragmentSubcomponent.class
      }
    )
    abstract class ApplicationModule {
      @Provides
      @IntoSet
      static Class<?> addToComponentHierarchy() {
        return ApplicationComponent.class;
      }

      @Binds
      @IntoMap
      @ActivityKey(TestActivity.class)
      abstract AndroidInjector.Factory<Activity, ?> bindFactoryForTestActivity(
          ActivitySubcomponent.Builder builder);

      @Binds
      @IntoMap
      @FragmentKey(TestParentFragment.class)
      abstract AndroidInjector.Factory<Fragment, ?> bindFactoryForParentFragment(
          ParentFragmentSubcomponent.Builder builder);

      @Binds
      @IntoMap
      @FragmentKey(TestChildFragment.class)
      abstract AndroidInjector.Factory<Fragment, ?> bindFactoryForChildFragment(
          ChildFragmentSubcomponent.Builder builder);
    }

    @Subcomponent(modules = ActivitySubcomponent.ActivityModule.class)
    interface ActivitySubcomponent extends AndroidInjector<TestActivity> {
      @Module
      abstract class ActivityModule {
        @Provides
        @IntoSet
        static Class<?> addToComponentHierarchy() {
          return ActivitySubcomponent.class;
        }
      }

      @Subcomponent.Builder
      abstract class Builder extends AndroidInjector.Builder<Activity, TestActivity> {}
    }

    @Subcomponent(modules = ParentFragmentSubcomponent.ParentFragmentModule.class)
    interface ParentFragmentSubcomponent extends AndroidInjector<TestParentFragment> {
      @Module
      abstract class ParentFragmentModule {
        @Provides
        @IntoSet
        static Class<?> addToComponentHierarchy() {
          return ParentFragmentSubcomponent.class;
        }
      }

      @Subcomponent.Builder
      abstract class Builder extends AndroidInjector.Builder<Fragment, TestParentFragment> {}
    }

    @Subcomponent(modules = ChildFragmentSubcomponent.ChildFragmentModule.class)
    interface ChildFragmentSubcomponent extends AndroidInjector<TestChildFragment> {
      @Module
      abstract class ChildFragmentModule {
        @Provides
        @IntoSet
        static Class<?> addToComponentHierarchy() {
          return ChildFragmentSubcomponent.class;
        }
      }

      @Subcomponent.Builder
      abstract class Builder extends AndroidInjector.Builder<Fragment, TestChildFragment> {}
    }
  }
}
