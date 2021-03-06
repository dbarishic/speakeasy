<script>
  import Icon from "fa-svelte";
  import { faVolumeUp } from "@fortawesome/free-solid-svg-icons";

  import { getLanguagesAsync } from "./Utils.js";

  const BASE_API_URL =
    "https://u7t88w3dal.execute-api.eu-west-1.amazonaws.com/Prod";

  export let textToTranslate = "";

  let selectedBackend = "espeak";
  let backends = ["espeak", "polly"];

  let languages;
  let selectedLanguage;
  let loading = false;

  document.addEventListener("DOMContentLoaded", async () => {
    languages = await getLanguagesAsync(selectedBackend);
  });

  const checkAndPlayFromCache = (language, text) => {
    let cachedLanguage = sessionStorage.getItem("lang-code");
    let cachedText = sessionStorage.getItem("translation-text");

    if (!cachedLanguage || !cachedText || !language || !text) {
      return false;
    }

    if (
      cachedLanguage.toLowerCase().trim() === language.toLowerCase().trim() &&
      cachedText.toLowerCase().trim() === text.toLowerCase().trim()
    ) {
      const audioAsBase64 = sessionStorage.getItem("base64audio");
      var audio = new Audio("data:audio/mpeg;base64," + audioAsBase64);
      audio.play();
      return true;
    }

    return false;
  };

  const loadLanguages = async () => {
    languages = await getLanguagesAsync(selectedBackend);
    selectedLanguage = languages[0];
  };

  const synthesize = async () => {
    if (textToTranslate.trim.length > 3000) {
      return;
    }

    const requestBody = {
      language: selectedLanguage.code,
      text: textToTranslate
    };

    let isCached = checkAndPlayFromCache(
      requestBody.language,
      requestBody.text
    );

    if (isCached) {
      return;
    }

    loading = true;

    let audioAsBase64 = null;
    if (selectedBackend === "espeak") {
      const response = await fetch(BASE_API_URL + "/espeak-synthesize?" + "language=" + selectedLanguage.code + "&text=" + textToTranslate, {
        method: "get"
      });

      audioAsBase64 = await response.text();
    } else if (selectedBackend === "polly") {
      const response = await fetch(BASE_API_URL + "/synthesize-voice", {
        method: "post",
        cache: "force-cache",
        body: JSON.stringify(requestBody)
      });

      audioAsBase64 = await response.text();
    }

    sessionStorage.setItem("lang-code", requestBody.language);
    sessionStorage.setItem("translation-text", requestBody.text);
    sessionStorage.setItem("base64audio", audioAsBase64);

    loading = false;

    var audio = new Audio("data:audio/mpeg;base64," + audioAsBase64);
    audio.play();
  };
</script>

<style>
  .synthesize-controls {
    display: flex;
    float: left;
    justify-content: left;
    margin-top: 2%;
    margin-right: 8px;
  }

  .synthesize-button {
    margin-left: 5px;
    display: inline-block;
    vertical-align: bottom;
    font-size: 2rem;
    text-align: center;
    height: 45px;
  }

  .synthesize-button:hover {
    color: #555;
  }

  @keyframes lds-rolling {
    0% {
      -webkit-transform: translate(-50%, -50%) rotate(0deg);
      transform: translate(-50%, -50%) rotate(0deg);
    }
    100% {
      -webkit-transform: translate(-50%, -50%) rotate(360deg);
      transform: translate(-50%, -50%) rotate(360deg);
    }
  }
  @-webkit-keyframes lds-rolling {
    0% {
      -webkit-transform: translate(-50%, -50%) rotate(0deg);
      transform: translate(-50%, -50%) rotate(0deg);
    }
    100% {
      -webkit-transform: translate(-50%, -50%) rotate(360deg);
      transform: translate(-50%, -50%) rotate(360deg);
    }
  }
  .lds-rolling {
    position: relative;
  }
  .lds-rolling div,
  .lds-rolling div:after {
    position: absolute;
    width: 100px;
    height: 100px;
    border: 20px solid #333;
    border-top-color: transparent;
    border-radius: 50%;
  }
  .lds-rolling div {
    -webkit-animation: lds-rolling 1s linear infinite;
    animation: lds-rolling 1s linear infinite;
    top: 100px;
    left: 100px;
  }
  .lds-rolling div:after {
    -webkit-transform: rotate(90deg);
    transform: rotate(90deg);
  }
  .lds-rolling {
    width: 40px !important;
    height: 40px !important;
    -webkit-transform: translate(-20px, -20px) scale(0.2) translate(20px, 20px);
    transform: translate(-20px, -20px) scale(0.2) translate(20px, 20px);
  }
</style>

{#if languages}
  <div class="synthesize-controls">
    <select bind:value={selectedBackend} on:change={loadLanguages}>
      {#each backends as backend}
        <option value={backend}>
          {@html backend}
        </option>
      {/each}
    </select>
  </div>
  <div class="synthesize-controls">
    <select bind:value={selectedLanguage}>
      {#each languages as language}
        <option value={language}>
          {@html language.name}
        </option>
      {/each}
    </select>
    {#if textToTranslate && !loading}
      <div
        class="synthesize-button"
        on:click={synthesize}
        on:touch={synthesize}>
        <Icon icon={faVolumeUp} />
      </div>
    {/if}
    {#if loading}
      <div class="lds-css ng-scope">
        <div style="width:100%;height:100%" class="lds-rolling">
          <div />
        </div>
      </div>
    {/if}
  </div>
{/if}
