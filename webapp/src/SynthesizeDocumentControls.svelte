<script>
  import Dropzone from "dropzone";
  import "../node_modules/dropzone/dist/min/dropzone.min.css";

  Dropzone.options.dropzoneUpload = {
    paramName: "file", // The name that will be used to transfer the file
    maxFilesize: 2, // MB,
    dictDefaultMessage: "<strong>Drag and Drop pdf file</strong> <br\> or click to upload",
    autoProcessQueue: false,
    init: function() {
      this.on("addedfile", function(file) {
        if (this.files.length > 1) {
          this.removeFile(this.files[0]);
        }
      });
    }
  };
</script>

<style>
  .label {
    text-align: left;
    font-size: 0.9rem;
    font-weight: 100;
    font-family: "Overpass";
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

  .btn {
    display: inline-block;
    background: transparent;
    color: inherit;
    font: inherit;
    border: 0;
    outline: 0;
    padding: 0;
    transition: all 200ms ease-in;
    cursor: pointer;
  }
  .btn--primary {
    background: #000;
    color: #fff;
    box-shadow: 0 0 10px 2px rgba(0, 0, 0, 0.1);
    border-radius: 2px;
    padding: 12px 36px;
  }
  .btn--primary:hover {
    background: #333;
  }
  .btn--primary:active {
    background: #333;
    box-shadow: inset 0 0 10px 2px rgba(0, 0, 0, 0.2);
  }
  .btn--inside {
    margin-left: -96px;
  }

  .form__field {
    width: 360px;
    background: #fff;
    color: #a3a3a3;
    font: inherit;
    box-shadow: 0 6px 10px 0 rgba(0, 0, 0, 0.1);
    border: 0;
    outline: 0;
    padding: 22px 18px;
  }

  .wrapper {
    margin-top: 2%;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    flex-direction: column;
  }

  .dropzone {
    background: white;
    border-radius: 5px;
    border: 2px dashed rgb(0, 0, 0);
    border-image: none;
    margin-left: auto;
    margin-right: auto;
    min-width: 50%;
    margin-bottom: 3%;
  }

  @media (min-width: 320px) and (max-width: 480px) {
    .form__field {
      width: 305px;
    }

    .dropzone {
      width: 100%;
    }

    .submit-container {
      margin-top: 15%;
    }
  }

  .submit-container {
    margin-top: 4%;
  }
</style>

<div class="wrapper">
  <form
    action="/file-upload"
    class="dropzone dropzone-preview form"
    id="dropzone-upload" />

  <div class="submit-container">
    <p class="label">Where do we send the processed file?</p>
    <div class="container">
      <div class="container__item">
        <input
          type="email"
          class="form__field"
          placeholder="Your E-Mail Address" />
        <button
          type="button"
          action="submit"
          class="btn btn--primary btn--inside uppercase">
          Submit
        </button>
      </div>
    </div>
  </div>
</div>
