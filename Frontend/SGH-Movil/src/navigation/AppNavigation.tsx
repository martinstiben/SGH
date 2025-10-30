import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import LandingScreen from '../screens/LandingScreen';
import LoginScreen from '../screens/LoginScreen';
import VerificationCodeScreen from '../screens/VerificationCodeScreen';
import SchedulesScreen from '../screens/SchedulesScreen';

export type RootStackParamList = {
  Landing: undefined;
  Login: undefined;
  VerificationCode: { email: string };
  Schedules: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function AppNavigation() {
  return (
    <Stack.Navigator initialRouteName="Landing" screenOptions={{ headerShown: false }}>
      <Stack.Screen name="Landing" component={LandingScreen} />
      <Stack.Screen name="Login" component={LoginScreen} />
      <Stack.Screen name="VerificationCode" component={VerificationCodeScreen} />
      <Stack.Screen name="Schedules" component={SchedulesScreen} />
    </Stack.Navigator>
  );
}
