package ru.terrakok.cicerone.sample.ui.animations;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.transition.ChangeBounds;
import android.view.View;

import javax.inject.Inject;

import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.android.support.SupportAppNavigator;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.Replace;
import ru.terrakok.cicerone.sample.R;
import ru.terrakok.cicerone.sample.SampleApplication;
import ru.terrakok.cicerone.sample.Screens;
import ru.terrakok.cicerone.sample.ui.animations.photos.SelectPhotoFragment;
import ru.terrakok.cicerone.sample.ui.animations.profile.ProfileFragment;
import ru.terrakok.cicerone.sample.ui.common.BackButtonListener;

/**
 * Created by Konstantin Tskhovrebov (aka @terrakok) on 14.07.17.
 */

public class ProfileActivity extends AppCompatActivity {
    public static final String PHOTO_TRANSITION = "photo_trasition";

    @Inject
    NavigatorHolder navigatorHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SampleApplication.INSTANCE.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_container);

        if (savedInstanceState == null) {
            navigator.applyCommands(new Command[]{new Replace(new Screens.ProfileInfoScreen())});
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        navigatorHolder.setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        navigatorHolder.removeNavigator();
        super.onPause();
    }

    private Navigator navigator = new SupportAppNavigator(this, R.id.container) {
        @Override
        protected void setupFragmentTransaction(Command command, Fragment currentFragment, Fragment nextFragment, FragmentTransaction fragmentTransaction) {
            if (command instanceof Forward
                    && currentFragment instanceof ProfileFragment
                    && nextFragment instanceof SelectPhotoFragment) {
                setupSharedElementForProfileToSelectPhoto(
                        (ProfileFragment) currentFragment,
                        (SelectPhotoFragment) nextFragment,
                        fragmentTransaction
                );
            }
        }
    };

    private void setupSharedElementForProfileToSelectPhoto(ProfileFragment profileFragment,
                                                           SelectPhotoFragment selectPhotoFragment,
                                                           FragmentTransaction fragmentTransaction) {
        ChangeBounds changeBounds = new ChangeBounds();
        selectPhotoFragment.setSharedElementEnterTransition(changeBounds);
        selectPhotoFragment.setSharedElementReturnTransition(changeBounds);
        profileFragment.setSharedElementEnterTransition(changeBounds);
        profileFragment.setSharedElementReturnTransition(changeBounds);

        View view = profileFragment.getAvatarViewForAnimation();
        fragmentTransaction.addSharedElement(view, PHOTO_TRANSITION);
        selectPhotoFragment.setAnimationDestinationId((Integer) view.getTag());
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment != null
                && fragment instanceof BackButtonListener
                && ((BackButtonListener) fragment).onBackPressed()) {
            return;
        } else {
            super.onBackPressed();
        }
    }
}
