<script>
  import Icon from "fa-svelte";
  import { faVolumeUp } from "@fortawesome/free-solid-svg-icons";

  const BASE_API_URL =
    "https://9cyxehf46g.execute-api.eu-west-1.amazonaws.com/Prod";

  export let textToTranslate = "";

  let languages;
  let selectedLanguage;

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
        "Accept": "audio/mpeg",
        "Content-Type": "application/json"
      }
    });

    const audioAsBase64 = await response.text();
    var audio = new Audio("data:audio/mpeg;base64," + audioAsBase64);
    audio.play();
  };

  const getLanguagesAsync = async () => {
    const response = await fetch(BASE_API_URL + "/get-languages", {
      method: "get",
      cache: "force-cache"
    });
    const data = await response.json();
    languages = data.languages;
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
    {#if textToTranslate}
      <div class="synthesize-button" on:click={synthesize}>
        <Icon icon={faVolumeUp} />
      </div>
    {/if}
  </div>
{/if}
