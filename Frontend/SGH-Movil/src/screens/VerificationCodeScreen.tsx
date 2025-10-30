import React, { useState } from 'react';
import {
  View,
  Image,
  Text,
  TextInput,
  TouchableOpacity,
  ImageBackground,
} from 'react-native';
import { useNavigation, useRoute, RouteProp } from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import { useAuth } from '../context/AuthContext';
import CustomAlert from '../components/Login/CustomAlert';
import { verificationStyles } from '../styles/verificationStyles';
import { RootStackParamList } from '../navigation/AppNavigation';

// Preload images to ensure they appear immediately
const userIcon = require('../assets/images/user.png');

type VerificationCodeRouteProp = RouteProp<RootStackParamList, 'VerificationCode'>;
type VerificationCodeNavProp = NativeStackNavigationProp<RootStackParamList, 'VerificationCode'>;

export default function VerificationCodeScreen() {
  const navigation = useNavigation<VerificationCodeNavProp>();
  const route = useRoute<VerificationCodeRouteProp>();
  const { email } = route.params;
  const { verifyCode } = useAuth();

  const [verificationCode, setVerificationCode] = useState('');
  const [loading, setLoading] = useState(false);

  const [alertVisible, setAlertVisible] = useState(false);
  const [alertTitle, setAlertTitle] = useState('');
  const [alertMessage, setAlertMessage] = useState('');

  const handleVerifyCode = async () => {
    if (!verificationCode.trim()) {
      setAlertTitle('Código requerido');
      setAlertMessage('Por favor ingresa el código de verificación');
      setAlertVisible(true);
      return;
    }

    setLoading(true);
    try {
      await verifyCode({ email, code: verificationCode });

      // Navigate directly to schedules without showing alert
      navigation.replace('Schedules');
    } catch (error: any) {
      setAlertTitle('Código incorrecto');
      setAlertMessage('El código de verificación es incorrecto. Inténtalo de nuevo.');
      setAlertVisible(true);
    } finally {
      setLoading(false);
    }
  };

  return (
    <ImageBackground
      source={require('../assets/images/background.jpg')}
      style={verificationStyles.backgroundImage}
      resizeMode="cover"
    >
      <View style={verificationStyles.darkOverlay} pointerEvents="none" />
      <View style={verificationStyles.container}>
        <View style={verificationStyles.card}>
          <View style={verificationStyles.iconContainer}>
            <Image source={require('../assets/images/lock.png')} style={verificationStyles.icon} />
          </View>

          <Text style={verificationStyles.title}>Verificación de Código</Text>

          <Text style={verificationStyles.subtitle}>
            Hemos enviado un código de verificación a tu correo electrónico para completar el inicio de sesión.
          </Text>

          <Text style={verificationStyles.emailText}>
            Código enviado a: {email}
          </Text>

          <TextInput
            style={verificationStyles.codeInput}
            placeholder="000000"
            placeholderTextColor="#ccc"
            value={verificationCode}
            onChangeText={setVerificationCode}
            keyboardType="numeric"
            maxLength={6}
            textAlign="center"
          />

          <TouchableOpacity
            style={[verificationStyles.verifyButton, loading && verificationStyles.verifyButtonDisabled]}
            onPress={handleVerifyCode}
            disabled={loading}
          >
            <Text style={verificationStyles.verifyButtonText}>
              {loading ? 'Verificando...' : 'Verificar código'}
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={verificationStyles.backButton}
            onPress={() => navigation.goBack()}
          >
            <Text style={verificationStyles.backButtonText}>
              Volver al inicio de sesión
            </Text>
          </TouchableOpacity>
        </View>
      </View>

      <CustomAlert
        visible={alertVisible}
        title={alertTitle}
        message={alertMessage}
        onClose={() => setAlertVisible(false)}
      />
    </ImageBackground>
  );
}