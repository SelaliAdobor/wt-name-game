package com.willowtree.namegame.screens.namegame;

import android.graphics.Color;
import android.view.View;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.Image;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaEdge;
import com.willowtree.namegame.R;
import com.willowtree.namegame.api.profiles.Profile;
import com.willowtree.namegame.util.PicassoImage;

@LayoutSpec
public class ProfileLayoutSpec {

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @Prop Profile profile,
                                    @Prop Runnable clickEventHandler) {

        return Column.create(c)
                .paddingDip(YogaEdge.ALL, 16)
                .backgroundColor(Color.WHITE)
                .child(
                        PicassoImage.create(c)
                                .imageUrl(profile.getHeadshot().get().getSanitizedUrl().get()) //TODO: Expose as top level property if used in more locations
                                .errorDrawableRes(R.drawable.unloaded_profile_image)
                                .placeholderImageRes(R.drawable.unloaded_profile_image)
                )
                .build();
    }

    @OnEvent(ClickEvent.class)
    static void onClick(
            ComponentContext c,
            @FromEvent View view,
            @Prop Runnable clickEventHandler) {
        clickEventHandler.run();
    }

}