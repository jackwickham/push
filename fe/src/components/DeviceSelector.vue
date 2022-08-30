<script setup lang="ts">
import { getFirebaseDatabase } from "@/firebase";
import { ref as firebaseRef, onValue } from "firebase/database";
import { computed, ref, watchEffect } from "vue";
import { map } from "lodash-es";
import type { Device } from "@/types/types";
import DeviceRadio from "./DeviceRadio.vue";

type PushRegistration = {
  deviceName: string;
  notificationToken: string;
};

const props = defineProps<{
  userId: string;
  modelValue?: string;
  disabled: boolean;
}>();
const emit = defineEmits<{
  (e: "update:modelValue", value: string | undefined): void;
}>();

const devices = ref<Device[] | undefined>(undefined);

const database = getFirebaseDatabase();
watchEffect(() => {
  const dbRef = firebaseRef(
    database,
    `users/${props.userId}/pushRegistrations`
  );
  onValue(dbRef, (snapshot) => {
    const data = snapshot.val();
    devices.value = map(data, (v: PushRegistration, k: string) => ({
      name: v.deviceName,
      id: k,
    }));
  });
});

const selectedDevice = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit("update:modelValue", value);
  },
});
</script>

<template>
  <div class="device-selector">
    <p class="small">Devices</p>
    <div v-if="devices !== undefined">
      <DeviceRadio
        v-for="device in devices"
        v-bind:key="device.id"
        :device="device"
        name="device"
        v-model="selectedDevice"
        :disabled="props.disabled"
      />
    </div>
    <div v-else>
      <p>Loading...</p>
    </div>
  </div>
</template>
