<script>
  import Icon from "fa-svelte";
  import { faVolumeUp } from "@fortawesome/free-solid-svg-icons";

  const BASE_API_URL =
    "https://9cyxehf46g.execute-api.eu-west-1.amazonaws.com/Prod";

  export let textToTranslate = "";

  let languages;
  let selectedLanguage;
  let loading = false;

  document.addEventListener("DOMContentLoaded", () => {
    getLanguagesAsync();
  });

  const synthesize = async () => {
    if (textToTranslate.trim.length > 3000) {
      return;
    }

    const json = {
      language: selectedLanguage.code,
      message: textToTranslate
    };

    const response = await fetch(BASE_API_URL + "/synthesize-voice", {
      method: "post",
      cache: "force-cache",
      body: JSON.stringify(json),
      headers: {
        Accept: "audio/mpeg",
        "Content-Type": "application/json"
      }
    });

    loading = true;
    const audioAsBase64 = await response.text();

    loading = false;
    var audio = new Audio("data:audio/mpeg;base64," + audioAsBase64);
    audio.play();
  };

  const getLanguagesAsync = async () => {
    const cachedLanguages = sessionStorage.getItem("languages");
    console.log(cachedLanguages);
    if (cachedLanguages !== null) {
      languages = JSON.parse(cachedLanguages);
      return;
    }

    const response = await fetch(BASE_API_URL + "/get-languages", {
      method: "get",
      cache: "force-cache",
      headers: {
        Accept: "audio/mpeg"
      }
    });
    const data = await response.json();
    languages = data.languages;

    sessionStorage.setItem("languages", JSON.stringify(languages));
  };
</script>

<style>
  .synthesize-controls {
    float: left;
  }

  .synthesize-button {
    display: inline-block;
    vertical-align: bottom;
    font-size: 2rem;
    text-align: center;
    height: 45px;
  }

  .synthesize-button:hover {
    color: #555;
  }

  .lds-facebook {
    display: inline-block;
    position: relative;
    width: 40px;
    height: 40px;
  }
  .lds-facebook div {
    display: inline-block;
    position: absolute;
    left: 6px;
    width: 13px;
    background: black;
    animation: lds-facebook 1.2s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  }
  .lds-facebook div:nth-child(1) {
    left: 6px;
    animation-delay: -0.24s;
  }
  .lds-facebook div:nth-child(2) {
    left: 26px;
    animation-delay: -0.12s;
  }
  .lds-facebook div:nth-child(3) {
    left: 45px;
    animation-delay: 0;
  }
  @keyframes lds-facebook {
    0% {
      top: 6px;
      height: 51px;
    }
    50%,
    100% {
      top: 19px;
      height: 26px;
    }
  }
</style>

{#if languages}
  <div class="synthesize-controls">
    <select bind:value={selectedLanguage}>
      {#each languages as language}
        <option value={language}>
          {@html language.name}
        </option>
      {/each}
    </select>
    {#if textToTranslate && !loading}
      <div class="synthesize-button" on:click={synthesize} on:touch={synthesize}>
        <Icon icon={faVolumeUp} />
      </div>
    {/if}
    {#if loading}
      <div class="lds-facebook">
        <div />
        <div />
        <div />
      </div>
    {/if}
  </div>
{/if}
