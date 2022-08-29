<script setup lang="ts">
import { onAuthStateChanged, type User } from '@firebase/auth';
import { useRouter } from 'vue-router';
import { getFirebaseAuth } from '@/firebase';
import MessageInput from '../components/MessageInput.vue';
import DeviceSelector from '../components/DeviceSelector.vue';
import { ref } from 'vue';
import Callout from '../components/Callout.vue';
import { CalloutType } from '@/types/types';
import { isObject } from '@vue/shared';

const router = useRouter();
const auth = getFirebaseAuth();

const user = ref<User | undefined>(undefined);

onAuthStateChanged(auth, (updatedUser) => {
  if (!updatedUser) {
    router.replace({ path: "/login" });
    user.value = undefined;
  } else {
    user.value = updatedUser;
  }
});

const message = ref("");
const selectedDevice = ref<string | undefined>(undefined);
const submitting = ref(false);
const error = ref<string | undefined>(undefined);
const sent = ref(false);

const onSubmit = async (e: Event) => {
  e.preventDefault();
  submitting.value = true;
  error.value = undefined;
  sent.value = false;

  try {
    const result = await fetch("http://localhost:9785/api/v1/send-push", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${await user.value!.getIdToken()}`,
      },
      body: JSON.stringify({
        deviceId: selectedDevice.value,
        payload: message.value,
      }),
    });
    if (result.status >= 300) {
      error.value = `${result.status}: ${await result.text()}`;
    } else {
      sent.value = true;
      message.value = "";
    }
  } catch(e) {
    error.value = isObject(e) ? e.message || e.name || String(e) : String(e);
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <main class="home">
    <form class="main-form" v-if="user !== undefined" @submit="onSubmit">
      <Callout v-if="error" :type="CalloutType.ERROR">
        {{ error }}
      </Callout>
      <Callout v-if="sent" :type="CalloutType.SUCCESS">
        Push sent successfully
      </Callout>
      <MessageInput v-model="message" :disabled="submitting" />
      <DeviceSelector :user-id="user.uid" v-model="selectedDevice" :disabled="submitting" />
      <input class="primary-button" type="submit" value="Send" :disabled="submitting" />
    </form>
    <p v-else>
      Loading...
    </p>
  </main>
</template>

<style>
  .home {
    font-size: 1.5em;
  }

  .main-form > *:not(:first-child) {
    margin-top: 1em;
  }

  .primary-button {
    font-size: 1em;
    background: #074d12;
    border: 1px solid #147924;
    color: #fff;
    padding: 4px 8px;
    border-radius: 5px;
    cursor: pointer;
  }

  .primary-button[disabled] {
    opacity: 0.6;
  }
</style>
