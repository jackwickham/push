<script setup lang="ts">
import type { Device } from "@/types/types";
import { computed } from "vue";

const props = defineProps<{
  device: Device;
  name: string;
  modelValue?: string;
  disabled: boolean;
}>();
const emit = defineEmits<{
  (e: "update:modelValue", value: string | undefined): void;
}>();

const value = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit("update:modelValue", value);
  },
});
</script>

<template>
  <div class="device-radio-wrapper">
    <input
      type="radio"
      :name="name"
      :value="device.id"
      :id="'device-' + device.id"
      v-model="value"
      required="true"
      :disabled="props.disabled"
    />
    <label :for="'device-' + device.id">{{ device.name }}</label>
  </div>
</template>

<style>
.device-radio-wrapper label {
  margin-left: 10px;
}

.device-radio-wrapper input {
  position: relative;
  top: -2px;
}
</style>
