import React from 'react';
import { ScrollView, Image, View, Text, TouchableOpacity, Dimensions } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { styles } from '../styles/landingStyles';
import Header from '../components/Genericos/Header';
import InfoCard from '../components/Genericos/InfoCard';
import StatCard from '../components/Genericos/StatCard';
import { RootStackParamList } from '../navigation/types';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';

type NavigationProp = NativeStackNavigationProp<RootStackParamList, 'Landing'>;

export default function LandingScreen() {
  const navigation = useNavigation<NavigationProp>();

  const handleLogin = () => {
    navigation.navigate('Login');
  };

  return (
    <ScrollView style={styles.mainContainer}>
      {/* Header moderno */}
      <Header
        title="SGH"
        buttonLabel="Ingresar"
        onPress={handleLogin}
      />

      {/* Sección Hero */}
      <View style={styles.heroSection}>
        <Image source={require('../assets/images/logo.png')} style={styles.heroLogo} />
        <Text style={styles.heroTitle}>Sistema de Gestión de Horarios</Text>
        <Text style={styles.heroSubtitle}>
          La solución más avanzada para la gestión inteligente de horarios académicos
        </Text>
      </View>

      {/* Características principales */}
      <View style={styles.featuresSection}>
        <Text style={styles.sectionTitle}>¿Por qué elegir SGH?</Text>
        <InfoCard
          items={[
            '🚀 Generación automática de horarios optimizados',
            '👨‍🏫 Gestión integral de profesores y asignaturas',
            '📊 Análisis y reportes en tiempo real',
            '🔒 Seguridad y privacidad de datos garantizada',
            '📱 Interfaz intuitiva y fácil de usar',
            '⚡ Procesamiento rápido y eficiente',
          ]}
        />
      </View>

      {/* Estadísticas destacadas */}
      <View style={styles.statsSection}>
        <Text style={styles.sectionTitle}>Resultados comprobados</Text>
        <View style={styles.statsContainer}>
          <StatCard
            number="100%"
            label="Automatización en la creación de horarios"
            icon={require('../assets/images/trophy.png')}
          />
          <StatCard
            number="0"
            label="Conflictos de horarios garantizados"
            icon={require('../assets/images/rocket.png')}
          />
        </View>
        <View style={styles.statsContainer}>
          <StatCard
            number="24/7"
            label="Disponibilidad del sistema"
            icon={require('../assets/images/shapes.png')}
          />
          <StatCard
            number="∞"
            label="Horarios personalizables"
            icon={require('../assets/images/user.png')}
          />
        </View>
      </View>

      {/* Sección de beneficios */}
      <View style={styles.benefitsSection}>
        <Text style={styles.sectionTitle}>Beneficios clave</Text>
        <View style={styles.benefitCard}>
          <Text style={styles.benefitTitle}>⏱️ Ahorra tiempo</Text>
          <Text style={styles.benefitDescription}>
            Reduce el tiempo de creación de horarios de días a minutos
          </Text>
        </View>
        <View style={styles.benefitCard}>
          <Text style={styles.benefitTitle}>🎯 Precisión total</Text>
          <Text style={styles.benefitDescription}>
            Elimina conflictos y errores humanos automáticamente
          </Text>
        </View>
        <View style={styles.benefitCard}>
          <Text style={styles.benefitTitle}>📈 Mejora continua</Text>
          <Text style={styles.benefitDescription}>
            Análisis continuo para optimizar la gestión académica
          </Text>
        </View>
      </View>

      {/* Call to Action */}
      <View style={styles.ctaSection}>
        <TouchableOpacity style={styles.ctaButton} onPress={handleLogin}>
          <Text style={styles.ctaButtonText}>Comenzar ahora</Text>
        </TouchableOpacity>
        <Text style={styles.ctaDescription}>
          Comienza a gestionar tus horarios de manera inteligente
        </Text>
      </View>

      {/* Footer */}
      <View style={styles.footer}>
        <Text style={styles.footerText}>© 2024 SGH - Sistema de Gestión de Horarios</Text>
      </View>
    </ScrollView>
  );
}
