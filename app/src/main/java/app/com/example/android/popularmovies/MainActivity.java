package app.com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainActivity extends ActionBarActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        String stringUrl = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=6ddc37bbc33a03d6e38e4935aa130385";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        new DownloadWebpageTask().execute(stringUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("DEBUG_TAG", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            ArrayList<Movie> movies = getMovies(result);
            ArrayList<String> titles = new ArrayList<String>();
            String[] tokens = result.split("title");
            ArrayList<String> list = new ArrayList<String>(Arrays.asList(tokens));
            GridView gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new GridAdapter(list));
        }

        private ArrayList<Movie> getMovies(String result) {
            try{
                result = "{\"page\":1,\"results\":[,{\"adult\":false,\"backdrop_path\":\"/dkMD5qlogeRMiEixC4YNPUvax2T.jpg\",\"genre_ids\":[28,12,878,53],\"id\":135397,\"original_language\":\"en\",\"original_title\":\"Jurassic World\",\"overview\":\"Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.\",\"release_date\":\"2015-06-12\",\"poster_path\":\"/uXZYawqUsChGSj54wcuBtEdUJbh.jpg\",\"popularity\":53.989445,\"title\":\"Jurassic World\",\"video\":false,\"vote_average\":7.0,\"vote_count\":935},{\"adult\":false,\"backdrop_path\":\"/tbhdm8UJAb4ViCTsulYFL3lxMCd.jpg\",\"genre_ids\":[53,28,12],\"id\":76341,\"original_language\":\"en\",\"original_title\":\"Mad Max: Fury Road\",\"overview\":\"An apocalyptic story set in the furthest reaches of our planet, in a stark desert landscape where humanity is broken, and most everyone is crazed fighting for the necessities of life. Within this world exist two rebels on the run who just might be able to restore order. There's Max, a man of action and a man of few words, who seeks peace of mind following the loss of his wife and child in the aftermath of the chaos. And Furiosa, a woman of action and a woman who believes her path to survival may be achieved if she can make it across the desert back to her childhood homeland.\",\"release_date\":\"2015-05-15\",\"poster_path\":\"/kqjL17yufvn9OVLyXYpvtyrFfak.jpg\",\"popularity\":30.629314,\"title\":\"Mad Max: Fury Road\",\"video\":false,\"vote_average\":7.8,\"vote_count\":1082},{\"adult\":false,\"backdrop_path\":\"/cUfGqafAVQkatQ7N4y08RNV3bgu.jpg\",\"genre_ids\":[28,18,53],\"id\":254128,\"original_language\":\"en\",\"original_title\":\"San Andreas\",\"overview\":\"In the aftermath of a massive earthquake in California, a rescue-chopper pilot makes a dangerous journey across the state in order to rescue his estranged daughter.\",\"release_date\":\"2015-05-29\",\"poster_path\":\"/qey0tdcOp9kCDdEZuJ87yE3crSe.jpg\",\"popularity\":28.120135,\"title\":\"San Andreas\",\"video\":false,\"vote_average\":6.3,\"vote_count\":347},{\"adult\":false,\"backdrop_path\":\"/yxR0mp83arFwlOuZDlqSRCFWs6S.jpg\",\"genre_ids\":[10751,16,12,35],\"id\":211672,\"original_language\":\"en\",\"original_title\":\"Minions\",\"overview\":\"Minions Stuart, Kevin and Bob are recruited by Scarlet Overkill, a super-villain who, alongside her inventor husband Herb, hatches a plot to take over the world.\",\"release_date\":\"2015-07-10\",\"poster_path\":\"/s5uMY8ooGRZOL0oe4sIvnlTsYQO.jpg\",\"popularity\":22.737465,\"title\":\"Minions\",\"video\":false,\"vote_average\":7.6,\"vote_count\":140},{\"adult\":false,\"backdrop_path\":\"/y5lG7TBpeOMG0jxAaTK0ghZSzBJ.jpg\",\"genre_ids\":[28,878,53],\"id\":198184,\"original_language\":\"en\",\"original_title\":\"Chappie\",\"overview\":\"Every child comes into the world full of promise, and none more so than Chappie: he is gifted, special, a prodigy. Like any child, Chappie will come under the influence of his surroundings—some good, some bad—and he will rely on his heart and soul to find his way in the world and become his own man. But there's one thing that makes Chappie different from any one else: he is a robot.\",\"release_date\":\"2015-03-06\",\"poster_path\":\"/saF3HtAduvrP9ytXDxSnQJP3oqx.jpg\",\"popularity\":19.801005,\"title\":\"Chappie\",\"video\":false,\"vote_average\":6.6,\"vote_count\":599},{\"adult\":false,\"backdrop_path\":\"/4liSXBZZdURI0c1Id1zLJo6Z3Gu.jpg\",\"genre_ids\":[878,14,28,12],\"id\":76757,\"original_language\":\"en\",\"original_title\":\"Jupiter Ascending\",\"overview\":\"In a universe where human genetic material is the most precious commodity, an impoverished young Earth woman becomes the key to strategic maneuvers and internal strife within a powerful dynasty…\",\"release_date\":\"2015-02-27\",\"poster_path\":\"/aMEsvTUklw0uZ3gk3Q6lAj6302a.jpg\",\"popularity\":19.678698,\"title\":\"Jupiter Ascending\",\"video\":false,\"vote_average\":5.4,\"vote_count\":795},{\"adult\":false,\"backdrop_path\":\"/2BXd0t9JdVqCp9sKf6kzMkr7QjB.jpg\",\"genre_ids\":[12,10751,16,28,35],\"id\":177572,\"original_language\":\"en\",\"original_title\":\"Big Hero 6\",\"overview\":\"The special bond that develops between plus-sized inflatable robot Baymax, and prodigy Hiro Hamada, who team up with a group of friends to form a band of high-tech heroes.\",\"release_date\":\"2014-11-07\",\"poster_path\":\"/3zQvuSAUdC3mrx9vnSEpkFX0968.jpg\",\"popularity\":19.42222,\"title\":\"Big Hero 6\",\"video\":false,\"vote_average\":7.9,\"vote_count\":1582},{\"adult\":false,\"backdrop_path\":\"/nkwoiSVJLeK0NI8kTqioBna61bm.jpg\",\"genre_ids\":[35],\"id\":214756,\"original_language\":\"en\",\"original_title\":\"Ted 2\",\"overview\":\"Newlywed couple Ted and Tami-Lynn want to have a baby, but in order to qualify to be a parent, Ted will have to prove he's a person in a court of law.\",\"release_date\":\"2015-06-26\",\"poster_path\":\"/A7HtCxFe7Ms8H7e7o2zawppbuDT.jpg\",\"popularity\":19.212703,\"title\":\"Ted 2\",\"video\":false,\"vote_average\":7.3,\"vote_count\":127},{\"adult\":false,\"backdrop_path\":\"/xu9zaAevzQ5nnrsXN6JcahLnG4i.jpg\",\"genre_ids\":[18,878],\"id\":157336,\"original_language\":\"en\",\"original_title\":\"Interstellar\",\"overview\":\"Interstellar chronicles the adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.\",\"release_date\":\"2014-11-05\",\"poster_path\":\"/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg\",\"popularity\":17.829412,\"title\":\"Interstellar\",\"video\":false,\"vote_average\":8.4,\"vote_count\":2624},{\"adult\":false,\"backdrop_path\":\"/qhH3GyIfAnGv1pjdV3mw03qAilg.jpg\",\"genre_ids\":[12,14],\"id\":122917,\"original_language\":\"en\",\"original_title\":\"The Hobbit: The Battle of the Five Armies\",\"overview\":\"Mere seconds after the events of \\\"Desolation\\\", Bilbo and Company continue to claim a mountain of treasure that was guarded long ago: But with Gandalf the Grey also facing some formidable foes of his own, the Hobbit is outmatched when the brutal army of orcs led by Azog the Defiler returns. But with other armies such as the elves and the men of Lake-Town, which are unsure to be trusted, are put to the ultimate test when Smaug's wrath, Azog's sheer strength, and Sauron's force of complete ends attack. All in all, the trusted armies have two choices: unite or die. But even worse, Bilbo gets put on a knife edge and finds himself fighting with Hobbit warfare with all of his might for his dwarf-friends, as the hope for Middle-Earth is all put in Bilbo's hands. The one \\\"precious\\\" thing to end it all.\",\"release_date\":\"2014-12-17\",\"poster_path\":\"/qrFwjJ5nvFnpBCmXLI4YoeHJNBH.jpg\",\"popularity\":17.353379,\"title\":\"The Hobbit: The Battle of the Five Armies\",\"video\":false,\"vote_average\":7.2,\"vote_count\":1544},{\"adult\":false,\"backdrop_path\":\"/9eKd1DDDAbrDNXR2he7ZJEu7UkI.jpg\",\"genre_ids\":[28,12,35,80],\"id\":207703,\"original_language\":\"en\",\"original_title\":\"Kingsman: The Secret Service\",\"overview\":\"Kingsman: The Secret Service tells the story of a super-secret spy organization that recruits an unrefined but promising street kid into the agency's ultra-competitive training program just as a global threat emerges from a twisted tech genius.\",\"release_date\":\"2015-02-13\",\"poster_path\":\"/oAISjx6DvR2yUn9dxj00vP8OcJJ.jpg\",\"popularity\":15.008929,\"title\":\"Kingsman: The Secret Service\",\"video\":false,\"vote_average\":7.7,\"vote_count\":1208},{\"adult\":false,\"backdrop_path\":\"/fii9tPZTpy75qOCJBulWOb0ifGp.jpg\",\"genre_ids\":[36,18,53,10752],\"id\":205596,\"original_language\":\"en\",\"original_title\":\"The Imitation Game\",\"overview\":\"Based on the real life story of legendary cryptanalyst Alan Turing, the film portrays the nail-biting race against time by Turing and his brilliant team of code-breakers at Britain's top-secret Government Code and Cypher School at Bletchley Park, during the darkest days of World War II.\",\"release_date\":\"2014-11-14\",\"poster_path\":\"/noUp0XOqIcmgefRnRZa1nhtRvWO.jpg\",\"popularity\":15.092948,\"title\":\"The Imitation Game\",\"video\":false,\"vote_average\":8.0,\"vote_count\":1330},{\"adult\":false,\"backdrop_path\":\"/szytSpLAyBh3ULei3x663mAv5ZT.jpg\",\"genre_ids\":[35,16,10751],\"id\":150540,\"original_language\":\"en\",\"original_title\":\"Inside Out\",\"overview\":\"Growing up can be a bumpy road, and it's no exception for Riley, who is uprooted from her Midwest life when her father starts a new job in San Francisco. Like all of us, Riley is guided by her emotions - Joy, Fear, Anger, Disgust and Sadness. The emotions live in Headquarters, the control center inside Riley's mind, where they help advise her through everyday life. As Riley and her emotions struggle to adjust to a new life in San Francisco, turmoil ensues in Headquarters. Although Joy, Riley's main and most important emotion, tries to keep things positive, the emotions conflict on how best to navigate a new city, house and school.\",\"release_date\":\"2015-06-19\",\"poster_path\":\"/rDycdoAXtBb7hoWlBpZqbwk2F44.jpg\",\"popularity\":15.022941,\"title\":\"Inside Out\",\"video\":false,\"vote_average\":8.4,\"vote_count\":298},{\"adult\":false,\"backdrop_path\":\"/xjjO3JIdneMBTsS282JffiPqfHW.jpg\",\"genre_ids\":[10749,14,10751,18],\"id\":150689,\"original_language\":\"en\",\"original_title\":\"Cinderella\",\"overview\":\"When her father unexpectedly passes away, young Ella finds herself at the mercy of her cruel stepmother and her daughters. Never one to give up hope, Ella's fortunes begin to change after meeting a dashing stranger in the woods.\",\"release_date\":\"2015-03-13\",\"poster_path\":\"/2i0JH5WqYFqki7WDhUW56Sg0obh.jpg\",\"popularity\":14.890223,\"title\":\"Cinderella\",\"video\":false,\"vote_average\":7.0,\"vote_count\":356},{\"adult\":false,\"backdrop_path\":\"/rFtsE7Lhlc2jRWF7SRAU0fvrveQ.jpg\",\"genre_ids\":[12,878,28],\"id\":99861,\"original_language\":\"en\",\"original_title\":\"Avengers: Age of Ultron\",\"overview\":\"When Tony Stark tries to jumpstart a dormant peacekeeping program, things go awry and Earth’s Mightiest Heroes are put to the ultimate test as the fate of the planet hangs in the balance. As the villainous Ultron emerges, it is up to The Avengers to stop him from enacting his terrible plans, and soon uneasy alliances and unexpected action pave the way for an epic and unique global adventure.\",\"release_date\":\"2015-05-01\",\"poster_path\":\"/t90Y3G8UGQp0f0DrP60wRu9gfrH.jpg\",\"popularity\":14.636249,\"title\":\"Avengers: Age of Ultron\",\"video\":false,\"vote_average\":7.8,\"vote_count\":1391},{\"adult\":false,\"backdrop_path\":\"/bHarw8xrmQeqf3t8HpuMY7zoK4x.jpg\",\"genre_ids\":[878,14,12],\"id\":118340,\"original_language\":\"en\",\"original_title\":\"Guardians of the Galaxy\",\"overview\":\"Light years from Earth, 26 years after being abducted, Peter Quill finds himself the prime target of a manhunt after discovering an orb wanted by Ronan the Accuser.\",\"release_date\":\"2014-08-01\",\"poster_path\":\"/9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg\",\"popularity\":14.187727,\"title\":\"Guardians of the Galaxy\",\"video\":false,\"vote_average\":8.2,\"vote_count\":2812},{\"adult\":false,\"backdrop_path\":\"/kJre98tnbNXbk5L5altHkQWGwD3.jpg\",\"genre_ids\":[28,12,878,9648],\"id\":158852,\"original_language\":\"en\",\"original_title\":\"Tomorrowland\",\"overview\":\"Bound by a shared destiny, a bright, optimistic teen bursting with scientific curiosity and a former boy-genius inventor jaded by disillusionment embark on a danger-filled mission to unearth the secrets of an enigmatic place somewhere in time and space that exists in their collective memory as \\\"Tomorrowland.\\\"\",\"release_date\":\"2015-05-22\",\"poster_path\":\"/69Cz9VNQZy39fUE2g0Ggth6SBTM.jpg\",\"popularity\":13.622631,\"title\":\"Tomorrowland\",\"video\":false,\"vote_average\":6.5,\"vote_count\":301},{\"adult\":false,\"backdrop_path\":\"/uCI4vNT4u9xOIU3pKV3qeaFcuO9.jpg\",\"genre_ids\":[27,53,18],\"id\":287424,\"original_language\":\"en\",\"original_title\":\"Maggie\",\"overview\":\"There's a deadly zombie epidemic threatening humanity, but Wade, a small-town farmer and family man, refuses to accept defeat even when his daughter Maggie becomes infected. As Maggie's condition worsens and the authorities seek to eradicate those with the virus, Wade is pushed to the limits in an effort to protect her. Joely Richardson co-stars in this post-apocalyptic thriller.\",\"release_date\":\"2015-05-08\",\"poster_path\":\"/xxX0v4vyfVc3Z8DEsbLJODnMOfQ.jpg\",\"popularity\":13.333465,\"title\":\"Maggie\",\"video\":false,\"vote_average\":4.7,\"vote_count\":99},{\"adult\":false,\"backdrop_path\":\"/6xjBbCZp3bXFps5ET1Y4zfq6bws.jpg\",\"genre_ids\":[14,35,16,878,10751],\"id\":228161,\"original_language\":\"en\",\"original_title\":\"Home\",\"overview\":\"When Earth is taken over by the overly-confident Boov, an alien race in search of a new place to call home, all humans are promptly relocated, while all Boov get busy reorganizing the planet. But when one resourceful girl, Tip (Rihanna), manages to avoid capture, she finds herself the accidental accomplice of a banished Boov named Oh (Jim Parsons). The two fugitives realize there’s a lot more at stake than intergalactic relations as they embark on the road trip of a lifetime.\",\"release_date\":\"2015-03-27\",\"poster_path\":\"/A59QUfxxhS6688W2dm0h00LXbzx.jpg\",\"popularity\":13.003956,\"title\":\"Home\",\"video\":false,\"vote_average\":7.2,\"vote_count\":207}],\"total_pages\":11663,\"total_results\":233260}";
                JSONObject obj = new JSONObject(result);
                Gson gson = new GsonBuilder().create();
                ArrayList<Movie> movies = new ArrayList<Movie>();
                for(int i=1;i<obj.getJSONArray("results").length();i++){
                    JSONObject movieOne = obj.getJSONArray("results").getJSONObject(i);
                    Movie movie = gson.fromJson(String.valueOf(movieOne), Movie.class);
                    movies.add(movie);
                }

                return movies;
            }
            catch (Exception e) {
                return null;
            }
        }
    }
}
