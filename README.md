# Skald

[![Download](https://api.bintray.com/packages/tangoagency/maven/skald/images/download.svg) ](https://bintray.com/tangoagency/maven/viking/_latestVersion) 
[![Build Status](https://travis-ci.org/TangoAgency/Skald.svg?branch=master)](https://travis-ci.org/TangoAgency/Skald) 
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/70f38ae0149147d7998efd9fc17c65a3)](https://www.codacy.com/app/TangoAgency/Skald?utm_source=github.com&utm_medium=referral&utm_content=TangoAgency/Skald&utm_campaign=badger)

<a href="https://github.com/TangoAgency/Skald"><img src="https://user-images.githubusercontent.com/469111/32132925-6d179078-bbcd-11e7-9c4e-d268e20247f7.png" width="300px"></a>
<br/><br/>
Skald library was implemented to deliver easy-to-use API for playing music in services such as Spotify and Deezer.
I decided to create this library in order to use music services in applications easily.

## Usage

#### Step 1
In order to use Spotify you need to copy spotify-player-*.aar file from [spotify Android SDK][SpotifySDK] 
to the libs folder in your app project.

If you would like to use Deezer you need to do the same thing. Deezer aar file can be found here: [deezer Android SDK][DeezerSDK]

After copying the files add gradle dependencies:
```groovy
    implementation "com.github.TangoAgency.Skald:core:{latest_release}"

    //if you want to use Spotify
    implementation "com.github.TangoAgency.Skald:spotify:{latest_release}"
    implementation 'com.spotify.android:auth:{latest_release}'
    implementation 'com.spotify.sdk:spotify-player-{latest_release}@aar'

    // if you want to use Deezer
    implementation "com.github.TangoAgency.Skald:deezer:{latest_release}"
    implementation 'com.deezer:deezer-sdk-{latest_release}@aar'

    //you also need this libraries
    implementation "com.squareup.retrofit2:converter-gson:{latest_release}"
    implementation 'io.reactivex.rxjava2:rxandroid:{latest_release}'
    implementation 'io.reactivex.rxjava2:rxjava:{latest_release}'
    implementation 'com.squareup.retrofit2:adapter-rxjava2:{latest_release}'
```

#### Step 2
When your application starts (e.g. onCreate method in your Application class) add providers which you are interested in:
```java
    SpotifyProvider spotifyProvider = new SpotifyProvider(this, SPOTIFY_CLIENT_ID,
        SPOTIFY_REDIRECT_URI, SPOTIFY_CLIENT_SECRET);
    DeezerProvider deezerProvider = new DeezerProvider(this, DEEZER_CLIENT_ID);

    Skald.with(spotifyProvider, deezerProvider);
```

#### Step 3
Now you are able to use Skald API. In order to log in use SkaldAuthService:
```java
    SkaldAuthService skaldAuthService = new SkaldAuthService(getApplicationContext(), new OnAuthErrorListener() {
        @Override
        public void onAuthError(AuthError authError) {
            if (authError.hasResolution()) {
                startActivityForResult(authError.getResolution(), AUTH_REQUEST_CODE);
            }
        }
    });

    skaldAuthService.login(SpotifyProvider.NAME)
```
If you want to play some music, use SkaldMusicService:
```java
    SkaldMusicService skaldMusicService = new SkaldMusicService(getApplicationContext());

    skaldMusicService.play(skaldPlayableEntity)
        .subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                Log.d(TAG, "Play completed");
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "Error during playing music", error);
            }
         });
```
To pause, resume or stop music do the following (just choose proper playback function):
```java
    skaldMusicService.pause()
        .subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                Log.d(TAG, "Pause completed");
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e(TAG, "Error during pausing", error);
            }
        });
```
### Step 4
Do not forget to release resources (e.g. within onDestroy method in your Activity)
```java
    @Override
    protected void onDestroy() {
        skaldMusicService.release();
        super.onDestroy();
    }
```

## Find what you like
With Skald you have possibility to search tracks and playlists by query so you can find and play whatever you like!<br/><br/>
Just use SkaldMusicService:
```java
    skaldMusicService.searchTracks("workout")
        .subscribe(new DisposableSingleObserver<List<SkaldTrack>>() {
            @Override
            public void onSuccess(List<SkaldTrack> skaldTracks) {
                play(skaldTracks.get(0);
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "Error during searching tracks", error);
            }
        });

    skaldMusicService.searchPlayLists("hip-hop")
        .subscribe(new DisposableSingleObserver<List<SkaldPlaylist>>() {
            @Override
            public void onSuccess(List<SkaldPlaylist> skaldPlaylists) {
                play(skaldPlaylists.get(0);
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "Error during searching playlists", error);
            }
        });

    private void play(SkaldPlayableEntity skaldPlayableEntity) {
        skaldMusicService.play(skaldPlayableEntity)
            .subscribe(new DisposableCompletableObserver() {
                @Override
                public void onComplete() {
                    Log.d(TAG, "Played complete");
                }

                @Override
                public void onError(Throwable error) {
                    Log.e(TAG, "Error during playing music", error);
                }
            });
      }
```

## Get track info
Playback listener gives you opportunity to get TrackMetadata which contains
useful track info. <br/><br/>
You can achieve this by adding and implementing OnPlaybackListener class:
```java
    skaldMusicService.addOnPlaybackListener(new OnPlaybackListener() {
        @Override
        public void onPlayEvent(TrackMetadata trackMetadata) {
            Log.d(TAG, String.format("%s - %s", trackMetadata.getArtistsName(),
                trackMetadata.getTitle()));

            Picasso
                .with(this)
                .load(trackMetadata.getImageUrl())
                .into(trackImage);
        }

        @Override
        public void onPauseEvent() {

        }

        @Override
        public void onResumeEvent() {

        }

        @Override
        public void onStopEvent() {

        }

        @Override
        public void onError(PlaybackError playbackError) {
            Log.e(TAG, String.format("Playback error occurred %s", playbackError.getMessage()));
        }
    });
```


## Getting Help

To report a specific problem or feature request, [open a new issue on Github](https://github.com/TangoAgency/avatar-view/issues/new).

## Company

[![Facebook](https://github.com/TangoAgency/avatar-view/blob/master/images/facebook.png)](https://www.facebook.com/TangoDigitalAgency)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[![Twitter](https://github.com/TangoAgency/avatar-view/blob/master/images/twitter.png)](https://twitter.com/Tango_Agency)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[![LinkedIn](https://github.com/TangoAgency/avatar-view/blob/master/images/linkedin.png)](https://www.linkedin.com/company/tango-digital-agency)

[Here](https://github.com/TangoAgency/) you can see open source work developed by Tango Agency.

Whether you're searching for a new partner or trusted team for creating your new great product we are always ready to start work with you.

You can contact us via contact@tango.agency.
Thanks in advance.

License
-------

    MIT License

    Copyright (c) 2016 Tango Agency

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

[SpotifySDK]: <https://github.com/spotify/android-sdk>
[DeezerSDK]: <https://developers.deezer.com/sdk/android>