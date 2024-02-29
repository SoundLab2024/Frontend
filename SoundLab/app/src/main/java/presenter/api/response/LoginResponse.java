package presenter.api.response;

public class LoginResponse {

    //private String accessToken; //Se la risposta include un token di accesso creiamo le get/set
    private int userId; // ID dell'utente

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
