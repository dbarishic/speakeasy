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

    loading = true;

    const response = await fetch(BASE_API_URL + "/synthesize-voice", {
      method: "post",
      cache: "force-cache",
      body: JSON.stringify(json),
      headers: {
        Accept: "audio/mpeg",
        "Content-Type": "application/json"
      }
    });

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
      method: "post",
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
    display: flex;
    float: left;
    justify-content: left;
    align-content: left;
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
            -webkit-transform: translate(-20px, -20px) scale(0.2)
              translate(20px, 20px);
            transform: translate(-20px, -20px) scale(0.2) translate(20px, 20px);
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
