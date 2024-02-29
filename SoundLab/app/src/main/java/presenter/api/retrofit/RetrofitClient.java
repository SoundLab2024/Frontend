package presenter.api.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class RetrofitClient {
    private static Retrofit instance = null;

    // Metodo per ottenere l'istanza di Retrofit
    public static Retrofit getClient(String url) {
        if (instance == null) {
            // Configura l'interceptor per il logging (utile in fase di sviluppo)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Crea un client OkHttpClient e aggiungi l'interceptor
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);  // Aggiunge l'interceptor per il logging

            // Costruisce l'oggetto Retrofit
            instance = new Retrofit.Builder()
                    .baseUrl(url)  // Imposta l'URL base per le chiamate API
                    .addConverterFactory(GsonConverterFactory.create())  // Aggiunge il convertitore Gson per la serializzazione dei dati
                    .client(httpClient.build())  // Imposta il client HTTP personalizzato con l'interceptor
                    .build();
        }
        return instance;
    }
}

