<script>
  import Modal from "./Modal.svelte";

  import Dropzone from "dropzone";
  import "../node_modules/dropzone/dist/min/dropzone.min.css";

  import { getLanguagesAsync } from "./Utils.js";

  const BASE_API_URL =
    "https://u7t88w3dal.execute-api.eu-west-1.amazonaws.com/Prod";

  let selectedBackend = "espeak"
  let selectedLanguage;
  let languages;
  let myDropzone;

  let showModal = false;

  let email = "";
  let fileName = "";
  let formInvalid = false;
  let prog = 0;

  Dropzone.autoDiscover = false;

  document.addEventListener("DOMContentLoaded", async () => {
    languages = await getLanguagesAsync();

    myDropzone = new Dropzone("div#dropzone-upload", {
      url: "/",
      addRemoveLinks: true,
      method: "PUT",
      paramName: "file", // The name that will be used to transfer the file
      maxFilesize: 50, // MB,
      acceptedFiles: "application/pdf",
      dictDefaultMessage:
        "<strong>Drag and Drop pdf file</strong> <br> or click to upload",
      autoProcessQueue: false,
      headers: {
        "Content-Type": "application/pdf"
      },
      init: function() {
        this.on("addedfile", function(file) {
          fileName = this.files[0].name;

          if (this.files.length > 1) {
            this.removeFile(this.files[0]);
          }
        });
        this.on("complete", function(file) {
          this.removeFile(file);
          prog = 0;
          showModal = false;
        });
        this.on("totaluploadprogress", function(progress) {
          prog = Math.trunc(progress);
        });
      }
    });
  });

  const validateForm = () => {
    if (myDropzone.files.length < 1) {
      formInvalid = true;
      return false;
    }

    formInvalid = false;
    showModal = true;
  };

  const submitForm = async () => {
    console.log("NOT IMPLEMENTED!");

    const requestBody = {
      email: email,
      fileName: fileName,
      language: selectedLanguage.code
    };

    const response = await fetch(BASE_API_URL + "/upload-file", {
      method: "post",
      cache: "force-cache",
      body: JSON.stringify(requestBody)
    });

    const data = await response.json();

    myDropzone.options.url = data.url;
    myDropzone.processQueue();
  };

  const modalConfirmedHandler = async () => {
    await submitForm();
    prog = 0;
  };

  const modalRejectedHandler = () => {
    myDropzone.removeFile(myDropzone.files[0]);
    showModal = false;
    prog = 0;
  };
</script>

<style>
  @import url("https://fonts.googleapis.com/css?family=Overpass:400,900&display=swap");
  @import url("https://fonts.googleapis.com/css?family=Chivo&display=swap");

  .label {
    font-size: 0.9rem;
    font-weight: 100;
    font-family: "Overpass", sans-serif;
    margin: 0;
  }

  .container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .uppercase {
    text-transform: uppercase;
  }

  .form__field {
    width: 360px;
    background: #fff;
    color: #a3a3a3;
    font: inherit;
    box-shadow: 0 6px 10px 0 rgba(0, 0, 0, 0.24);
    transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
    border: 0;
    outline: 0;
    padding: 22px 18px;
  }

  .form__field:hover {
    box-shadow: 0 6px 10px 0 rgba(0, 0, 0, 0.5);
  }

  .wrapper {
    margin-top: 2%;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    flex-direction: column;
  }

  .dropzone-style {
    background: white;
    border-radius: 5px;
    border: 2px dashed rgb(0, 0, 0);
    border-image: none;
    margin-left: auto;
    margin-right: auto;
    min-width: 50%;
    min-height: 20%;
    flex: 1;
    margin-bottom: 3%;
  }

  .dropzone .dz-message {
    margin: 4em 0;
  }

  .submit-container {
    margin-top: 20%;
    margin-bottom: 20%;
  }

  form {
    width: 50%;
  }

  .language-selection {
    text-align: left;
    margin: 0;
  }

  .file-missing-error-message {
    color: red;
    font-size: 12px;
    font-family: "Overpass";
    font-weight: 100;
  }

  @media (min-width: 320px) and (max-width: 480px) {
    .form {
      width: 100%;
    }

    .form__field {
      width: 305px;
    }

    .dropzone {
      width: 100%;
    }

    .submit-container {
      width: 100%;
      margin-top: 15%;
      bottom: 0;
      right: 0;
      left: 0;
      margin-bottom: 15%;
      position: inline-block;
    }

    form {
      width: 100%;
    }
  }

  /* 
    ##Device = Low Resolution Tablets, Mobiles (Landscape)
    ##Screen = B/w 481px to 767px
  */
  @media (min-width: 481px) and (max-width: 767px) {
    .form {
      width: 100%;
    }

    .form__field {
      width: 339px;
    }
  }

  @media (min-width: 768px) and (max-width: 1024px) {
    .form {
      width: 100%;
    }
  }

  @media (min-width: 320px) and (max-width: 480px) {
    .submit-container {
      margin-top: 30%;
    }
  }

  @media (max-width: 320px) {
    .btn--inside {
      margin: 0;
    }
  }
</style>

<div class="wrapper">
  <form on:submit|preventDefault={validateForm} class="form">
    <div id="dropzone-upload" class="dropzone dropzone-style">
      <div class="dz-default dz-message">
        <span>
          <strong>Drag and Drop pdf file</strong>
          <br />
          or click to upload
        </span>
      </div>
    </div>
    <div class="language-selection">
      {#if languages}
        <select bind:value={selectedLanguage}>
          {#each languages as language}
            <option value={language}>
              {@html language.name}
            </option>
          {/each}
        </select>
      {/if}
    </div>

    <div class="submit-container">
      <p class="label">Where do we send the processed file?</p>
      <div class="container">
        <div class="container__item">
          <input
            id="email-field"
            bind:value={email}
            type="email"
            required
            class="form__field"
            placeholder="Your E-Mail Address" />
          <button type="submit" class="btn btn--primary btn--inside uppercase">
            Submit
          </button>
        </div>
      </div>
      {#if formInvalid}
        <p id="file-error" class="file-missing-error-message">
          Please add a pdf file.
        </p>
      {/if}
    </div>
  </form>
</div>

{#if showModal}
  <Modal
    on:confirm={modalConfirmedHandler}
    on:reject={modalRejectedHandler}
    progress={prog}>
    <h2 slot="header">Are you sure?</h2>
    <span>
      Are you sure you want to synthesize
      <strong>{fileName}</strong>
      in
      <strong>{selectedLanguage.name}</strong>
      and send the processed file to
      <strong>{email}</strong>
    </span>
  </Modal>
{/if}
