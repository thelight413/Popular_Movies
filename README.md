# Popular_Movies
For Udacity, to show popular movies to user

##API Key Required
For this app to work, you need an API key from [TheMovieDb](https://www.themoviedb.org). You will need to create an account and 
provide some personal information to get an API key.


Add this code to the app/build.gradle file:
```
buildTypes.each {
        it.buildConfigField 'String', 'MovieDB_API_KEY', '"YourAPIkey"'
    }
   ```
