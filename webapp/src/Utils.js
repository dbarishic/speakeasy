const BASE_API_URL =
    "https://u7t88w3dal.execute-api.eu-west-1.amazonaws.com/Prod";

const getLanguagesAsync = async (backend) => {

    if (backend === "polly") {
        const cachedLanguages = sessionStorage.getItem("languages-polly");
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
        sessionStorage.setItem("languages-polly", JSON.stringify(data.languages));
        return data.languages;
    } else if (backend === "espeak") {
        const cachedLanguages = sessionStorage.getItem("languages-espeak");
        if (cachedLanguages !== null) {
            return JSON.parse(cachedLanguages);
        }
    
        const response = await fetch(BASE_API_URL + "/espeak-list-languages", {
            method: "get"
        });

        const data = await response.json();
        sessionStorage.setItem("languages-espeak", JSON.stringify(data.languages));
        return data.languages;
    }
};

export { getLanguagesAsync };