<script setup lang="ts">
import { ref } from 'vue';
import { getFirebaseAuth } from '@/firebase';
import { signInWithEmailAndPassword, onAuthStateChanged } from "firebase/auth";
import { isObject } from '@vue/shared';
import { useRouter } from 'vue-router';

const credentials = ref({
  email: "",
  password: "",
});
const submitting = ref(false);
const error = ref<string | null>(null);

const auth = getFirebaseAuth();
const router = useRouter();

const submit = async () => {
  submitting.value = true;
  try {
    const userCredential = await signInWithEmailAndPassword(auth, credentials.value.email, credentials.value.password);
    console.log(userCredential);
  } catch (e) {
    if (isObject(e) && !!e.message) {
      error.value = e.message;
    } else {
      error.value = "Unknown error";
    }
    submitting.value = false;
  }
};

onAuthStateChanged(auth, (user) => {
  if (user) {
    router.replace({ path: "/" });
  }
});
</script>

<template>
  <div class="login">
    <h1>Login</h1>
    <div class="login-form">
      <p v-if="error !== null" class="login-error">
        {{error}}
      </p>
      <div class="login-input-group">
        <label class="small" for="email">Email</label>
        <input name="email" type="email" v-model="credentials.email" :disabled="submitting" />
      </div>
      <div class="login-input-group">
        <label class="small" for="password">Password</label>
        <input name="password" type="password" v-model="credentials.password" :disabled="submitting" />
      </div>
      <button class="login-button" @click="submit" v-if="!submitting">Login</button>
      <button class="login-button login-button-disabled" @click="submit" v-if="submitting">Logging in...</button>
    </div>
  </div>
</template>

<style>
@media (min-width: 1024px) {
  .login {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 20px;
  }

  .login, .login input {
    font-size: 1.1rem;
  }

  .login-form {
    display: flex;
    flex-direction: column;
    gap: 10px;
    width: 350px;
  }

  .login-error {
    color: #cc2323;
    font-size: 0.8em;
  }
  
  .login-input-group label {
    display: block;
  }

  .login input {
    background-color: #333;
    color: #ddd;
    border: 1px solid #555;
    border-radius: 4px;
    outline: none;
    padding: 3px;
    width: 100%;
  }

  .login input:focus-visible, .login-button:focus-visible {
    border: 1px solid var( --color-border-focus);
  }

  .login-button {
    padding: 5px;
    background-color: #333;
    border: 1px solid hsla(160, 100%, 37%, 0.5);
    color: #ddd;
    border-radius: 4px;
    cursor: pointer;
    margin-top: 5px;
    outline: none;
  }

  .login-button-disabled {
    opacity: 0.7;
  }
}
</style>
