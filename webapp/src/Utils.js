const BASE_API_URL =
    "https://9cyxehf46g.execute-api.eu-west-1.amazonaws.com/Prod";

const getLanguagesAsync = async () => {
    const cachedLanguages = sessionStorage.getItem("languages");
    if (cachedLanguages !== null) {
        return JSON.parse(cachedLanguages);
    }

    const requestBody = {
        origin: "speakeasy.FE"
    };

    const response = await fetch(BASE_API_URL + "/get-languages", {
        method: "post",
        cache: "force-cache",
        body: JSON.stringify(requestBody),
        headers: {
            Accept: "audio/mpeg"
        }
    });
    const data = await response.json();
    sessionStorage.setItem("languages", JSON.stringify(data.languages));
    return data.languages;

};

export { getLanguagesAsync };